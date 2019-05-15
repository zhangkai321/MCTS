package code.core.system.Solver;

import code.core.controller.ATCCmd;
import code.core.simulation.Scenario;
import code.core.system.Conflict;
import code.core.system.Detector;
import code.enums.Result;
import code.util.Global;

import java.util.HashMap;

public class SolverTree {
    int timeStep= Scenario.clock.getTimeStep();
    int totalCount = 0;
    double maxAlt = 12000.0;
    double minAlt = 6000.0;
    double hdgMax=50;
    double altStep = 300;
    double spdStep = 10 * Global.KT2MPS;
    double hdgStep=10;
    int nodeDuration = 165;

    Conflict conflict;
    Detector detector;
    TreeActionNode root;
    public Result result=null;

    public HashMap<Integer,ATCCmd> cmdList1=new HashMap<>();
    public HashMap<Integer,ATCCmd> cmdList2=new HashMap<>();

    public SolverTree(Conflict conflict, Detector detector){
            this.conflict = conflict;
            this.detector = detector;
            this.root = new TreeActionNode(conflict.state1.aircraft.curState,conflict.state2.aircraft.curState,this);
    }

    public void iteration(int step){
        for (; this.totalCount < step;this.totalCount++){
            iterationStep();
        }
        chooseResult();
    }
    private void iterationStep(){
        TreeActionNode node = this.root;
        boolean ok = false;
        while (!node.getChildren().isEmpty()){
            if (node.isAllChildrenExpanded()){
                node = node.mostValuableChildren(node);
                continue;
            }
            int idx = TreeNode.rd.nextInt( node.getChildren().size());
            node = node.getChildren().get(idx);
            ok = node.simulation(2) == Result.Solved;
            break;
        }
        if (node.getParent() != null){
            if (node.getParent() instanceof TreeActionNode){
                ((TreeActionNode) node.getParent()).checkAllChildrenExpanded();
            }
        }
        TreeNode n = node;
        while (n != null){
            n.setPlayCount(n.getPlayCount() + 1);
            if (ok){
                n.setOkCount(n.getOkCount()+1);
            }
            if (n instanceof TreeActionNode){
                ((TreeActionNode) n).updateValue(this.totalCount);
            }
            n = n.getParent();
        }
    }
    private void chooseResult(){
        TreeActionNode node = this.root;

        for (int i = 0; i <3; i++){
            if (node != null) {
                node = node.mostValuableChildren(node);
                if(node == null){
                    break;
                }
            }
            if(node.action != null){
                ATCCmd cmd=node.action.cmd;
                System.out.println("height:"+node.getHeight()+","+node.action.aircraft.id+","+cmd+","+node.result);
                if(node.action.aircraft == conflict.acft1){
                    cmdList1.put(cmd.getAssignTime(),cmd);
                }
                else {
                    cmdList2.put(cmd.getAssignTime(),cmd);
                }
            }
            this.result = node.result;

            if (this.result == Result.Solved || this.result==Result.Failed) {
                break;
            }
        }
    }
}
