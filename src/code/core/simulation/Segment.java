package code.core.simulation;

import code.enums.Direction;
import code.util.GeoUtil;

public class Segment {
    private double distance;
    private double heading;

    public String id=null;
    public WayPoint start;
    public WayPoint end;
    public Direction direction=Direction.Forward;

    public Segment(WayPoint start, WayPoint end){
        this.start = start;
        this.end = end;
        this.distance = GeoUtil.calDist(start.lng,start.lat,end.lng,end.lat);
        this.heading = GeoUtil.calHeading(start.lng,start.lat,end.lng,end.lat);
        setId();
    }
    public Segment(String id, WayPoint start, WayPoint end){
        this(start,end);
        this.id=id;
    }

    private void setId(){
        if (this.id==null){
            this.id="SEG# "+start.id+"-"+end.id;
        }
    }

    public double getHeading(){
        if (direction.equals(Direction.Forward)){
            return heading;
        }
        return GeoUtil.fixAngle(heading - 180);
    }
    public double getDistance(){return distance;}
    public WayPoint getEnd(){
        if (direction.equals(Direction.Forward)){
            return end;
        }
        return start;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Segment){
            Segment bs = (Segment)obj;
            if (bs.start.equals(this.start) && bs.end.equals(this.end))
             return  true;
        }
        return false;
    }
    public String toString(){
        return "id: "+id+","+"Start:("+start.lng+","+start.lat+"),End:("+end.lng+","+end.lat+")";
    }
    public int hashCode(){
        return start.hashCode()+end.hashCode();
    }
}
