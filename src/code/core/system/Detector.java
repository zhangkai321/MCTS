package code.core.system;

import code.core.aircraft.Position;
import code.core.aircraft.State;
import code.core.simulation.Segment;
import code.enums.Result;
import code.util.GeoUtil;
import code.util.Global;
import demo.spatialindex.rtree.RTree;
import demo.spatialindex.rtree.Rect;
import demo.spatialindex.rtree.SpatialData;
import code.enums.LateralType;

import java.util.*;

public class Detector {
    private static double LNG_GAP = 0.30522119469282;
    private static double LAT_GAP = 0.269494881006263;
    private static double ALT_GAP = 300;
    double sameRoutingThreshold = 10 * Global.KM2M;
    double crossThreshold = 20 * Global.KM2M;
    double oppositeThreshold = 30 * Global.KM2M;

    RTree tree;
    List<State> states=new ArrayList<>();
    List<Conflict> conflicts=new ArrayList<>();

    /** 构造函数 **/
    public Detector(){}
    public Detector(List<State> states){
        this.states=states;
        this.tree = new RTree(9,states);
        run();
    }

    /** Main函数 **/
    public void run(){
        for (State state:states) {
            List<SpatialData> resList = RoughtQuery(state);
            ExactQuery(state,resList);
        }
    }

    private List<SpatialData> RoughtQuery(State state){
        Rect c = new Rect(state.location.lng-LNG_GAP,state.location.lng + LNG_GAP,
                state.location.lat-LAT_GAP,state.location.lat+LAT_GAP,
                state.alt - ALT_GAP, state.alt + ALT_GAP);
        List<SpatialData> candidate = tree.search(c);
        return candidate;
    }
    private void ExactQuery(State state1, List<? extends SpatialData> resList){
        for (SpatialData state2:resList){
            Conflict conflict = detect(state1,(State) state2);
            if (!this.conflicts.contains(conflict)){
                this.conflicts.add(conflict);
            }
        }
    }
    public Conflict detect(State state1, State state2){
        double dist = GeoUtil.calDist(state1.location,state2.location);
        if(state1.finished || state2.finished || Math.abs(state1.alt - state2.alt) >= 300 || dist > oppositeThreshold){
            return null;
        }
        LateralType lType = computeLateralType(state1, state2);
        return lType !=null?new Conflict(state1,state2,lType,dist):null;
    }
    private LateralType computeLateralType(State state1, State state2) {
        Position pos1 = state1.pos;
        Position pos2 = state2.pos;

        if(pos1 == pos2){
            return LateralType.SameRouting;
        }
        Segment seg1 = pos1.getCurrentSegment();
        Segment seg2 = pos2.getCurrentSegment();
        if (seg1.equals(seg2)){
            if(seg1.direction.equals(seg2.direction)){
                return LateralType.SameRouting;
            }
            return LateralType.Opposite;
        }

        if (pos2.segments.size() > 1){
            if(pos2.getNextSegment() != null){
                Segment _seg2 = pos2.getNextSegment();
                if(seg1.equals(_seg2)){
                    if (seg1.direction.equals(_seg2.direction)){
                        return LateralType.SameRouting;
                    }
                    return LateralType.Opposite;
                }
            }
            if(pos2.getLastSegment() != null){
                Segment _seg2 = pos2.getLastSegment();
                if(seg1.equals(_seg2)){
                    if (seg1.direction.equals(_seg2.direction)){
                        return LateralType.SameRouting;
                    }
                    return LateralType.Opposite;
                }
            }

        }else if (pos1.segments.size() > 1){
            if(pos1.getNextSegment() != null){
                Segment _seg1 = pos1.getNextSegment();
                if(seg2.equals(_seg1)){
                    if (seg2.direction.equals(_seg1.direction)){
                        return LateralType.SameRouting;
                    }
                    return LateralType.Opposite;
                }
            }
            if(pos1.getLastSegment() != null){
                Segment _seg1 = pos1.getLastSegment();
                if(seg2.equals(_seg1)){
                    if (seg2.direction.equals(_seg1.direction)){
                        return LateralType.SameRouting;
                    }
                    return LateralType.Opposite;
                }
            }
        }
        double delta=Math.abs(state1.heading-state2.heading);
        delta=delta>180?Math.abs(delta-360):delta;
        if (delta<45){
            return LateralType.SameRouting;
        }
        if (delta>135){
            return LateralType.Opposite;
        }
        return LateralType.Cross;
    }

    public Result checkConflictSolved(Conflict c, State state1, State state2,int height){
        if (Math.abs(state1.alt-state2.alt)>=300){
            return Result.Solved;
        }
        double dist = GeoUtil.calDist(state1.location,state2.location);
        double threshold=c.lateralType==LateralType.Cross?crossThreshold:(c.lateralType==LateralType.SameRouting?sameRoutingThreshold:oppositeThreshold);
        if (dist < threshold){
            return Result.Failed;
        }
        return Result.UnSolved;
    }
}
