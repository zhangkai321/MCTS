package code.core.controller;

import code.core.aircraft.Aircraft;
import code.core.aircraft.State;
import code.core.simulation.Scenario;
import code.core.system.Conflict;
import code.core.system.Detector;
import code.core.system.Solver.SolverTree;
import code.enums.Result;
import code.util.GeoUtil;

public class Controller {
    private Detector detector=new Detector();
    public SolverTree tree;
    public Conflict conflict;
    public Aircraft A= Scenario.A;
    public Aircraft B= Scenario.B;

    public double minimum_distance=50000.0;
    public int minimum_time=-1;

    public void doStep(){
        Monitor();
        Brain();
    }

    private void Monitor(){
        if (A.curState!=null && B.curState!=null && !A.isFinished() && !B.isFinished()){
            double dist= GeoUtil.calDist(A.curState.location,B.curState.location);
            if (dist<minimum_distance){
                minimum_distance=dist;
                minimum_time= Scenario.clock.getTime();
            }
            System.out.println("now_dist: "+dist/1000.0+" KM(Controller_doStep)");
        }
        State state0 = A.finalState;
        State state1 = B.finalState;
        if (state0 == null || state1 == null){
            return;
        }
        if (state0.time != state1.time){
            return;
        }
        this.conflict= detector.detect(state0,state1);
    }
    private void Brain(){
        if (this.conflict != null) {
            System.out.println("-------------------------------------------------");
            System.out.println("Conflict information:(Controller_doStep)");
            System.out.println("Lateral_type: "+this.conflict.lateralType + "\n" +
                               "This_time:"+ Scenario.clock.getTime() + "\n" +
                               "That_time:"+this.conflict.state1.time+"\n"+
                               "That_time:"+this.conflict.state1.time+"\n"+
                               "A_"+this.conflict.state1+"\n" +
                               "B_"+this.conflict.state2+"\n" +
                               "That_time_dist: " + this.conflict.distance/1000.0+" KM");
            for (int i=0;i<10;i++){
                this.tree = new SolverTree(this.conflict,this.detector);
                tree.iteration(5000+i*100);
                System.out.println((i+1)+": "+tree.result);
                if (tree.result== Result.Solved){
                    A.commands=tree.cmdList1;
                    B.commands=tree.cmdList2;
                    break;
                }
            }

            System.out.println("\nSolution information:(SolverTree_dumpResult)");
            System.out.println("Result:" + tree.result);

            System.out.println("Cmd1 : ");
            for (int time: tree.cmdList1.keySet()){
                System.out.println("\t" + tree.cmdList1.get(time));
            }
            System.out.println("Cmd2 : ");
            for (int time: tree.cmdList2.keySet()){
                System.out.println("\t" + tree.cmdList2.get(time));
            }
            System.out.println("-------------------------------------------------");
        }
    }
}
