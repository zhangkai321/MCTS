package code.core.aircraft;

import code.core.simulation.Scenario;
import code.enums.FlightStage;
import kt.mas.core.traffic.aircraft.AircraftType;
import code.core.simulation.Routing;
import code.core.controller.ATCCmd;
import code.enums.VerticalState;

import java.util.HashMap;
import java.util.LinkedList;

public class Aircraft {
    public String id;
    public AircraftType type;
    public FlightPlan curFltPlan;
    public TrajectoryEngine trajectoryEngine;
    public LinkedList<State> prediction;
    public State curState=null;
    public State finalState=null;
    public FlightStage stage = FlightStage.NotStarted;

    public HashMap<Integer,State> realTracks=new HashMap<>();
    public HashMap<Integer,ATCCmd> commands=new HashMap<>();

    public Aircraft(String id, AircraftType type) {
        this.id = id;
        this.type = type;
        this.prediction = new LinkedList<>();
        this.trajectoryEngine = new TrajectoryEngine(this);
    }

    public void doStep(){
        if(!checkStage()){
            return;
        }
        System.out.println(this.curState+"(Aircraft_doStep)");
        predict();
        if(this.prediction.size() == 0){
            return;
        }
        this.realTracks.put(this.curState.time,curState);

        this.curState = this.prediction.removeFirst();
        if (this.prediction.size()>=1){
            this.finalState=this.prediction.removeLast();
        }
        if(this.curState.finished){
            this.stage = FlightStage.Done;
        }

    }
    private boolean checkStage(){
        if (this.stage == FlightStage.Done){
            System.out.println("(over)"+this.curState+"(Aircraft_checkStage)");
            return false;
        }
        if(this.curFltPlan == null){
            this.stage = FlightStage.NotStarted;
            System.out.println("NO Plan!(Aircraft_checkStage)");
            return false;
        }
        if (this.curFltPlan != null){
            int now = Scenario.clock.getTime();
            if (this.stage == FlightStage.NotStarted){
                if (now >= this.curFltPlan.startTime){
                    this.stage = FlightStage.Running;
                    initState(now);
                    return true;
                }
                System.out.println("State: "+this.id+"'s time is still early!(Aircraft_checkStage)");
            }

        }
        return this.stage == FlightStage.Running;
    }
    private void initState(int now){
        FlightPlan curFltPlan = this.curFltPlan;
        Routing routing = curFltPlan.routing;
        curState = new State(this);
        curState.time = now;
        curState.location =routing.getPoints().get(0);

        curState.fltPlan = curFltPlan;
        curState.pos = new Position(routing,0,curState.location);
        curState.heading = curState.pos.getCurrentSegment().getHeading();
        curState.alt = curFltPlan.RFL;
        curState.performance = this.type.getPerformanceByAlt(curState.alt);
        curState.vState = VerticalState.Cruise;
        curState.hSpdTAS = curState.performance.getNormCruiseTAS();
        this.trajectoryEngine.routing=routing;
    }
    private void predict(){
        prediction=new LinkedList<>();
        int timeStep = Scenario.clock.getTimeStep();
        State last =this.curState;

        int t0 = last.time+timeStep;
        int t1 = t0+ 300;

        this.trajectoryEngine.count=0;
        this.trajectoryEngine.check=this.trajectoryEngine.OppositeTurn;
        for (; t0<= t1 && !last.finished; t0+=timeStep){
            ATCCmd cmd= commands.get(t0)!=null?commands.get(t0):null;
            last= trajectoryEngine.doStep(last,cmd,timeStep);
            prediction.add(last);
        }
    }

    public boolean isFinished(){
        return stage == FlightStage.Done;
    }
}
