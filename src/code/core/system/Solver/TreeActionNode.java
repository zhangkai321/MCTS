package code.core.system.Solver;

import code.core.aircraft.Aircraft;
import code.core.aircraft.State;
import code.core.aircraft.TrajectoryEngine;
import code.core.controller.*;
import code.core.simulation.Scenario;
import code.enums.Result;
import code.enums.VerticalState;
import kt.mas.core.traffic.aircraft.FlightPerformance;

import java.util.ArrayList;
import java.util.List;

public class TreeActionNode implements TreeNode{
    public double value;
    private int playCount;
    private int okCount;
    private int height;
    private SolverTree tree;
    public CmdPackage action;
    private TreeNode parent;
    public State state1;
    public State state2;
    private List<TreeActionNode> children;
    private List<TreeActionNode> mChildren;

    public boolean allChildrenExpanded;
    public Result result;
    private List<State> tracks1 = new ArrayList<>();
    private List<State> tracks2 = new ArrayList<>();

    public TreeActionNode(State state1, State state2, SolverTree tree){
        this.state1 = state1;
        this.state2 = state2;
        this.tree = tree;
        this.children = new ArrayList<>();
        this.parent = null;
        this.height = 0;
        this.allChildrenExpanded = false;
        this.value = 0.0;
        this.playCount = 0;
        this.okCount = 0;
        initRoot();
    }
    public TreeActionNode(CmdPackage action, TreeNode parent, SolverTree tree){
        this.action = action;
        this.parent = parent;
        this.tree = tree;
        this.mChildren = new ArrayList<>();
        this.height = parent.getHeight() + 1;
        this.value = 0;
        this.playCount = 0;
        this.okCount = 0;
        this.allChildrenExpanded = false;
    }

    private void initRoot(){
        this.children = computeChildren(this);
    }
    private void initChildren(){
        this.mChildren = computeChildren(this);
    }

    private void checkResult(){
        for(int i=0;i<tracks1.size();i++){
            this.result = this.tree.detector.checkConflictSolved(this.tree.conflict,tracks1.get(i),tracks2.get(i),this.height);
            if (this.result== Result.Failed || this.result==Result.Solved){
                return;
            }
        }
        if (this.height>=2){
            this.result=Result.Solved;
        }
    }
    private List<TreeActionNode> computeChildren(TreeNode node){
        List<TreeActionNode> mChildren = new ArrayList<>();
        mChildren.addAll(computeAltActionChildren(node.getState1(),node));
        mChildren.addAll(computeAltActionChildren(node.getState2(),node));
        mChildren.addAll(computeSpdActionChildren(node.getState1(),node));
        mChildren.addAll(computeSpdActionChildren(node.getState2(),node));
        mChildren.addAll(computeHdgActionChildren(node.getState1(),node));
        mChildren.addAll(computeHdgActionChildren(node.getState2(),node));
        return mChildren;
    }
    private List<TreeActionNode> computeAltActionChildren(State state, TreeNode node){
        List<TreeActionNode> mChildren = new ArrayList<>();
        Aircraft acft = state.aircraft;
        double RFL = acft.curFltPlan!=null?acft.curFltPlan.RFL:null;
        SolverTree tree = node.getTree();
        double maxAlt = Math.min(RFL + 900,tree.maxAlt);
        double minAlt = Math.max(RFL - 900,tree.minAlt);
        if(state.altCmd != null){
            if(state.altCmd.delta > 0){
                minAlt = Math.max(minAlt,state.altCmd.targetAlt);
            }
            else {
                maxAlt = Math.min(maxAlt,state.altCmd.targetAlt);
            }
        }
        int dAlt1 = (int) (minAlt - RFL);
        dAlt1 = (int) (dAlt1/tree.altStep *tree.altStep);

        double alt = RFL + dAlt1;
        CmdPackage action =new CmdPackage(acft,new AltCmd((this.height+1)*this.tree.nodeDuration+ Scenario.clock.getTime()+1,0,RFL));
        while (alt <= maxAlt){
            mChildren.add(new TreeActionNode(action,node,node.getTree()));
            action=new CmdPackage(acft,new AltCmd((this.height+1)*90+ Scenario.clock.getTime()+1,alt-RFL,alt));
            alt += tree.altStep;
        }
        return mChildren;
    }
    private List<TreeActionNode> computeSpdActionChildren(State state, TreeNode node){
        List<TreeActionNode> mChildren = new ArrayList<>();
        Aircraft acft = state.aircraft;
        FlightPerformance performance = state.performance;
        SolverTree tree = node.getTree();
        double maxSpd;
        double minSpd;
        if (state.vState == VerticalState.Climb){
            maxSpd = performance.getMaxClimbTAS();
            minSpd = performance.getMinClimbTAS();
        }else if (state.vState == VerticalState.Cruise){
            maxSpd = performance.getMaxCruiseTAS();
            minSpd = performance.getMinCruiseTAS();
        }else {
            maxSpd = performance.getMaxDescentTAS();
            minSpd = performance.getMinDescentTAS();
        }
        double baseSpd = state.hSpdTAS;
        if(state.spdCmd != null){
            baseSpd = state.spdCmd.targetTAS;
            if (state.spdCmd.delta > 0){
                minSpd = Math.max(minSpd,state.spdCmd.targetTAS);
            }
            else {
                maxSpd = Math.min(maxSpd,state.spdCmd.targetTAS);
            }
        }
        double spd = baseSpd - 5 * tree.spdStep;
        CmdPackage action = new CmdPackage(acft,new SpdCmd((this.height+1)*this.tree.nodeDuration+ Scenario.clock.getTime()+1,0,state.hSpdTAS));
        while (true){
            if (spd > maxSpd){
                break;
            }
            if (spd >= minSpd){
                mChildren.add(new TreeActionNode(action,node,node.getTree()));
                action = new CmdPackage(acft,new SpdCmd((this.height+1)*90+ Scenario.clock.getTime()+1,spd-baseSpd,spd));
            }
            spd+=tree.spdStep;
        }
        return mChildren;
    }
    private List<TreeActionNode> computeHdgActionChildren(State state, TreeNode node){
        List<TreeActionNode> mChildren = new ArrayList<>();
        Aircraft acft = state.aircraft;
        SolverTree tree = node.getTree();

        double baseHdg=state.heading;
        double heading=baseHdg-5*tree.hdgStep;
        while (heading<=baseHdg+tree.hdgMax){
            CmdPackage action = new CmdPackage(acft,new HdgCmd((this.height+1)*this.tree.nodeDuration+ Scenario.clock.getTime()+1,heading-baseHdg,heading));
            mChildren.add(new TreeActionNode(action,node,node.getTree()));
            heading+=tree.hdgStep;
        }
        return mChildren;
    }

    public State getState1(){
        if (this.tracks1.size() == 0){
            computeTracks();
        }
        return this.tracks1.get(this.tracks1.size()-1);
    }
    public State getState2(){
        if (this.tracks2.size() == 0){
            computeTracks();
        }
        return this.tracks2.get(this.tracks2.size()-1);
    }
    private void computeTracks(){
        TreeNode parent = this.parent;
        if(parent == null){
            tracks1.addAll(computeTrack(this.state1));
            tracks2.addAll(computeTrack(this.state2));
        }
        else {
            tracks1.addAll(computeTrack(parent.getState1()));
            tracks2.addAll(computeTrack(parent.getState2()));
        }
        checkResult();
    }
    private List<State> computeTrack(State start){
        this.tree.nodeDuration=this.parent==null?30:165;
        List<State> track = new ArrayList<>();
        TrajectoryEngine engine = start.aircraft.trajectoryEngine;
        State prevState = start;
        State nextState = start;
        ATCCmd cmd =(action != null && action.aircraft == start.aircraft)?action.cmd:null;

        for (int i=0;i<tree.nodeDuration && !nextState.finished;i+=tree.timeStep){
            nextState = engine.doStep(prevState,cmd,tree.timeStep);
            track.add(nextState);
            prevState = nextState;
            cmd = null;
        }
        return track;
    }

    public void updateValue(double totalCount){
        this.value =playCount==0?0:Math.sqrt(Math.log(totalCount)*2.0/playCount)+ okCount/playCount;
    }
    public void checkAllChildrenExpanded(){
        List<TreeActionNode> temp=this.children==null?this.mChildren:this.children;
        this.allChildrenExpanded = true;

        for (TreeNode c : temp){
            if (c.getPlayCount() == 0){
                this.allChildrenExpanded = false;
                break;
            }
        }
    }
    public Result simulation(int maxHeight){
        List<TreeActionNode> children =  this.getChildren();
        if(this.height == maxHeight || this.result == Result.Solved){
            return this.result;
        }
        return children.get(rd.nextInt(children.size())).simulation(maxHeight);
    }
    public TreeActionNode mostValuableChildren (TreeNode  tNode){
        TreeActionNode ret = (TreeActionNode) tNode.getChildren().get(0);
        for (TreeNode c : tNode.getChildren()){
            if(((TreeActionNode) c).value > ret.value && ((TreeActionNode) c).value != 0.0){
                ret = (TreeActionNode)c;
            }
        }
        return ret;
    }

    @Override
    public SolverTree getTree() {
        return this.tree;
    }
    @Override
    public int getHeight() {
        return this.height;
    }
    @Override
    public TreeNode getParent() {
        return this.parent;
    }
    @Override
    public int getPlayCount() {
        return this.playCount;
    }
    @Override
    public void setPlayCount(int count) {
        this.playCount = count;
    }
    @Override
    public int getOkCount() {
        return this.okCount;
    }
    @Override
    public void setOkCount(int count) {
        this.okCount = count;
    }
    @Override
    public List<TreeActionNode> getChildren() {
        if (this.parent == null){
            return children;
        }
        if (this.mChildren == null || this.mChildren.isEmpty()){
            initChildren();
        }
        return this.mChildren;
    }
    @Override
    public boolean isAllChildrenExpanded() {
        return this.allChildrenExpanded;
    }
}
