package code.core.aircraft;

import code.core.controller.ATCCmd;
import code.core.controller.AltCmd;
import code.core.controller.HdgCmd;
import code.core.controller.SpdCmd;
import code.core.simulation.Routing;
import code.core.simulation.Scenario;
import code.core.simulation.Segment;
import code.core.simulation.WayPoint;
import code.enums.VerticalState;
import code.util.GeoUtil;
import code.util.Global;
import kt.mas.core.traffic.aircraft.FlightPerformance;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryEngine {
    double normVSpdAcc = 100 * Global.FTPM2MPS;
    double normHSpdAcc = 1.5 * Global.KT2MPS;
    double normHSpdDec = normHSpdAcc;
    double TurnThreshold = 0.3;
    int TimeThreshold;
    int count;
    int count2=0;
    boolean OppositeTurn=false;
    boolean check;
    public Aircraft aircraft;
    public List<WayPoint> wayPoints;
    public int number;
    public int number2;
    public Routing routing;
    public int forTest=0;

    public TrajectoryEngine(Aircraft aircraft){
        this.aircraft = aircraft;
    }

    public State doStep(State curState, ATCCmd atcCmd, int timeStep){
        this.wayPoints=curState.fltPlan.routing.getPoints();
        State nextState = new State(aircraft);
        nextState.time = curState.time + timeStep;
        nextState.fltPlan = curState.fltPlan;
        assignCmd(curState,nextState,atcCmd);
        verticalMove(curState,nextState,timeStep);
        nextState.performance = aircraft.type.getPerformanceByAlt(nextState.alt);
        horizontalMove(curState,nextState,timeStep);
        return nextState;
    }

    private void assignCmd(State curState, State nextState, ATCCmd atcCmd){
        AltCmd altCmd = null;
        SpdCmd spdCmd = null;
        HdgCmd hdgCmd = null;

        if (atcCmd != null ) {
            if (atcCmd instanceof AltCmd) {
                altCmd = (AltCmd) atcCmd;
            }
            else if (atcCmd instanceof SpdCmd) {
                spdCmd = (SpdCmd) atcCmd;
            }
            else if (atcCmd instanceof HdgCmd){
                hdgCmd= (HdgCmd) atcCmd;
            }
        }
        nextState.altCmd = altCmd == null ?curState.altCmd: altCmd;
        nextState.spdCmd = spdCmd == null ?curState.spdCmd: spdCmd;
        nextState.hdgCmd = hdgCmd == null ?curState.hdgCmd: hdgCmd;
    }

    private void verticalMove(State curState, State nextState, int timeStep){
        double targetAlt = nextState.altCmd == null ? nextState.fltPlan.RFL:nextState.altCmd.targetAlt;

        if (curState.alt==targetAlt){
            nextState.alt = curState.alt;
            nextState.vSpd = 0;
        }
        else if(curState.alt < targetAlt){
            climb(curState,nextState,targetAlt,timeStep);
        }
        else{
            descent(curState,nextState,targetAlt,timeStep);
        }

        nextState.vState=nextState.vSpd==0.0? VerticalState.Cruise:(nextState.vSpd<0.0? VerticalState.Descent: VerticalState.Climb);
    }
    private double climb(State curState, State nextState, double targetAlt, int timeStep){
        FlightPerformance performance = curState.performance;
        double altDiff = targetAlt - curState.alt;
        double prevVSpd = curState.vSpd;
        double normVSpd = performance.getNormClimbRate();
        double reqVSpd = altDiff * 2.0 * normVSpdAcc/prevVSpd;

        double nextVSpd;
        if(prevVSpd >= reqVSpd){
            nextVSpd = Math.max(0.0,prevVSpd-normVSpdAcc*timeStep);
        }
        else if(prevVSpd == normVSpd){
            nextVSpd = prevVSpd;
        }
        else if(prevVSpd < normVSpd){
            nextVSpd = Math.min(normVSpd,prevVSpd+normVSpdAcc*timeStep);
        }
        else {
            nextVSpd = Math.max(normVSpd, prevVSpd-normVSpdAcc*timeStep);
        }

        double vDist = (prevVSpd+nextVSpd)*timeStep/2.0;
        if(vDist>=altDiff){
            vDist = altDiff;
            nextVSpd = 0.0;
        }
        nextState.vSpd = nextVSpd;
        nextState.alt = curState.alt +vDist;
        return vDist;
    }
    private double descent(State curState, State nextState, double targetAlt, int timeStep){
        FlightPerformance performance = curState.performance;
        double altDiff = targetAlt - curState.alt;
        double prevVSpd = curState.vSpd;
        double normVSpd = -performance.getNormClimbRate();
        double reqVSpd = prevVSpd==0 ? Double.NEGATIVE_INFINITY:altDiff * 2.0 * -normVSpdAcc/prevVSpd;

        double nextVSpd;
        if(prevVSpd <= reqVSpd){
            nextVSpd = Math.min(0.0,prevVSpd+normVSpdAcc*timeStep);
        }
        else if(prevVSpd == normVSpd){
            nextVSpd = prevVSpd;
        }
        else if(prevVSpd > normVSpd){
            nextVSpd = Math.max(normVSpd,prevVSpd-normVSpdAcc*timeStep);
        }
        else {
            nextVSpd = Math.min(normVSpd, prevVSpd+normVSpdAcc*timeStep);
        }

        double vDist = (prevVSpd+nextVSpd)*timeStep/2.0;
        if(vDist<=altDiff){
            vDist = altDiff;
            nextVSpd = 0.0;
        }
        nextState.vSpd = nextVSpd;
        nextState.alt = curState.alt +vDist;
        return vDist;
    }

    private void horizontalMove(State curState, State nextState, int timeStep){
        FlightPerformance performance = curState.performance;
        double prevHSpd = curState.hSpdTAS;

        double normHSpd;
        double maxHSpd;
        double minHSpd;
        if(nextState.vState == VerticalState.Climb){
            normHSpd = performance.getNormClimbTAS();
            maxHSpd = performance.getMaxClimbTAS();
            minHSpd = performance.getMinClimbTAS();
        }
        else if(nextState.vState == VerticalState.Descent){
            normHSpd = performance.getNormDescentTAS();
            maxHSpd = performance.getMaxDescentTAS();
            minHSpd = performance.getMinDescentTAS();
        }
        else {
            normHSpd = performance.getNormCruiseTAS();
            maxHSpd = performance.getMaxCruiseTAS();
            minHSpd = performance.getMinCruiseTAS();
        }

        double targetSpd = nextState.spdCmd == null?normHSpd:(nextState.spdCmd.targetTAS > normHSpd?Math.min(nextState.spdCmd.targetTAS,maxHSpd):Math.max(nextState.spdCmd.targetTAS,minHSpd));
        double nextHSpd = targetSpd > prevHSpd?Math.min(prevHSpd+normHSpdAcc*timeStep, targetSpd):(targetSpd==prevHSpd?prevHSpd:Math.max(prevHSpd-normHSpdDec*timeStep,targetSpd));

        nextState.hSpdTAS = nextHSpd;
        double hDist = (prevHSpd + nextHSpd)*timeStep/2.0;
        makeTurn(curState,nextState,hDist,timeStep);
    }
    private void makeTurn(State curState, State nextState, double hDist, int timeStep){
        double normTurnRate = curState.performance.getNormTurnRate();
        double headingDiff=nextState.hdgCmd!=null?nextState.hdgCmd.targetHeading-curState.heading:GeoUtil.calIntersectionAngle(curState.pos.headingToTarget,curState.heading);
        double turnAngle =headingDiff > TurnThreshold?Math.min(headingDiff,normTurnRate*timeStep):(headingDiff < TurnThreshold?Math.max(headingDiff,-normTurnRate*timeStep):0.0);

        double nextHeading = GeoUtil.fixAngle(curState.heading+turnAngle);
        nextState.heading = nextHeading;
        nextState.turnRate = turnAngle/timeStep;

        double temp=turnAngle==0.0?curState.heading:(curState.heading+nextState.heading)/2.0;
        nextState.location = GeoUtil.getDestination(curState.location,temp,hDist);

        if (nextState.hdgCmd!=null){
            this.number=curState.pos.currentSegmentIndex;
            nextState.pos=new Position();
            nextState.pos.currentSegmentIndex=this.number;
            this.count++;
            int Delta_Time=-Scenario.clock.getTime()+nextState.hdgCmd.getAssignTime()+60;
            this.TimeThreshold=Delta_Time>=0 && Delta_Time<60?Delta_Time:60;
            if (this.count>this.TimeThreshold){
                for (int i=number+1;i<Math.min(number+6,this.wayPoints.size()-1);i++){
                    WayPoint wayPoint=this.wayPoints.get(i);
                    double h1=GeoUtil.calHeading(nextState.location,wayPoint);
                    h1=Math.abs(GeoUtil.calIntersectionAngle(h1,curState.heading));
                    if (h1<90){
                        nextState.pos=new Position(this.routing,i-1,nextState.location);
                        this.forTest= (int) (nextState.pos.distanceToTarget/nextState.hSpdTAS)+nextState.time-20;
                        double h2=GeoUtil.calHeading(wayPoint,this.wayPoints.get(i+1));
                        h2=Math.abs(GeoUtil.calIntersectionAngle(h2,nextState.pos.headingToTarget));
                        if (h2>90){
                            this.number2=i;
                            this.OppositeTurn=true;
                        }
                        break;
                    }
                }
                nextState.hdgCmd=null;
                this.count=0;
            }
        }
        else if (this.check && nextState.time>=this.forTest){
            nextState.pos=new Position();
            nextState.pos.headingToTarget=nextState.heading;
            nextState.pos.currentSegmentIndex=this.number2-1;
            this.count2++;
            int Delta_Time2=-Scenario.clock.getTime()+this.forTest+200;
            int TimeThreshold2=Delta_Time2>=0 && Delta_Time2<200?Delta_Time2:200;
            this.OppositeTurn=Delta_Time2<0?false:true;
            if (this.count2>=TimeThreshold2){
                nextState.pos=new Position(this.routing,this.number2-1,nextState.location);
                this.check=false;
                this.count2=0;
            }
        }
        else {
            nextState.pos = new Position(this.routing,curState.pos.currentSegmentIndex,nextState.location);
            checkPosition(nextState,timeStep);
        }
    }
    private boolean checkPosition(State nextState, int timeStep){
        Position pos = nextState.pos;
        if(passCurrentSegment(nextState,pos.getNextSegment(),timeStep)){
            if (pos.isOnFinalSegment()){
                return nextState.finished = true;
            }
            pos.currentSegmentIndex++;
            pos.updateDistanceAndHeading(nextState.location);
        }
        return false;
    }
    private boolean passCurrentSegment(State state, Segment nextSeg, double timeStep){
        double distanceToTarget = state.pos.distanceToTarget;
        if(distanceToTarget > 6 * Global.KM2M){
            return false;
        }
        if(distanceToTarget < state.hSpdTAS * timeStep){
            return true;
        }
        if (nextSeg != null) {
            double prediction = calcTurnPrediction(
                    state.hSpdTAS,
                    state.heading,
                    GeoUtil.calHeading(state.location,nextSeg.getEnd()),
                    state.performance.getNormTurnRate());
            if(distanceToTarget < prediction){
                return true;
            }
        }
        return Math.abs(GeoUtil.calIntersectionAngle(state.heading,state.pos.headingToTarget))>90.0;
    }
    private double calcTurnPrediction(double spd, double heading, double nextHeading, double turnRate){
        double turnRadianRate = turnRate* Global.Deg2Rad;
        double turnAngle = Math.abs(GeoUtil.calIntersectionAngle(heading,nextHeading));
        double turnRadian = turnAngle * Global.Deg2Rad;
        double turnRadius = spd/turnRadianRate;
        return turnRadius * Math.tan(turnRadian/2)/1.3;
    }
}
