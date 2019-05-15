package code.core.simulation;

import java.util.ArrayList;
import java.util.List;

public class Routing{
    public String id=null;
    private List<WayPoint> points;
    private List<Segment> segments=new ArrayList<>();
    public double totalDistance=0;

    public Routing(List<WayPoint> points){
        this.points = points;
        buildSegments();
        calcDistance();
        setId();
    }
    public Routing(String id, List<WayPoint> points){
        this(points);
        this.id=id;
    }

    private void calcDistance(){
        for (int i = 0; i < segments.size(); i++){
            this.totalDistance += segments.get(i).getDistance();
        }
    }
    private void buildSegments(){
        WayPoint lastPoint =null;
        for (WayPoint point:this.points){
            if(lastPoint != null) {
                this.segments.add(new Segment(lastPoint,point));
            }
            lastPoint =point;
        }
    }
    private void setId(){
        if (this.id==null){
            List<String> names=new ArrayList<>();
            for (Segment segment:segments){
                names.add(segment.id.split("SEG# ")[1]);
            }
            this.id=String.join("-",names);
        }
    }

    public List<WayPoint> getPoints() {
        return points;
    }
    public List<Segment> getSegments() {
        return segments;
    }
}
