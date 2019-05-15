package code.core.aircraft;

import code.core.simulation.WayPoint;
import code.enums.Direction;
import code.util.GeoUtil;
import code.core.simulation.Routing;
import code.core.simulation.Segment;

import java.util.List;

public class Position {
    public int currentSegmentIndex = 0;
    public double distanceToTarget;//
    public double headingToTarget;//

    public WayPoint target;//
    public Routing routing;//
    public List<Segment> segments;

    public Position(){
    }
    public Position(Routing routing, int currentSegmentIndex, double distanceToTarget, double headingToTarget){
        this.routing = routing;
        if(routing == null) {
            System.out.println("routing is null");
        }
        this.currentSegmentIndex = currentSegmentIndex;
        this.distanceToTarget = distanceToTarget;
        this.headingToTarget = headingToTarget;
        this.segments = routing.getSegments();
        target = getTarget();
    }
    public Position(Routing routing, int currentSegmentIndex, WayPoint location){
        this(routing,currentSegmentIndex,0.0,0.0);
        updateDistanceAndHeading(location);
    }

    private WayPoint getTarget(){
        if (getCurrentSegment().direction != Direction.Forward) {
            return getCurrentSegment().start;
        }
        return getCurrentSegment().end;
    }
    public Segment getCurrentSegment(){
        return this.segments.get(currentSegmentIndex);
    }

    public void updateDistanceAndHeading(WayPoint location){
        this.distanceToTarget = GeoUtil.calDist(location,getTarget());
        this.headingToTarget = GeoUtil.calHeading(location,getTarget());
    }
    public boolean isOnFinalSegment(){
        return this.currentSegmentIndex >= this.segments.size()-1;
    }
    public Segment getNextSegment(){
        if(isOnFinalSegment()){
            return null;
        }
        return segments.get(currentSegmentIndex + 1);
    }
    public Segment getLastSegment(){
        if (currentSegmentIndex<=0 || segments.size()<=1){
            return null;
        }
        return segments.get(currentSegmentIndex-1);
    }
}
