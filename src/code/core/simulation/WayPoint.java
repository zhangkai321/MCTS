package code.core.simulation;

public class WayPoint{
    public String id;
    public double lng;
    public double lat;
    public double alt;

    public WayPoint(){

    }
    public WayPoint(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }
    public WayPoint(double lng,double lat,double alt){
        this(lng, lat);
        this.alt=alt;
    }
    public WayPoint(String id, double lng, double lat) {
        this(lng, lat);
        this.id = id;
    }

    public int hashCode(){
        long lngBits = Double.doubleToLongBits(lng);
        long latBits = Double.doubleToLongBits(lat);
        long altBits=Double.doubleToLongBits(alt);
        lngBits = lngBits ^ lngBits>>>32;
        latBits = latBits ^ latBits>>>32;
        altBits=altBits^altBits>>>32;
        return ((((int)lngBits*31 + (int)latBits)*31+(int)altBits)*31)+id.hashCode();
    }
    public String toString(){
        return "(" + id + "," + lng + "," + lat +")";
    }
    public boolean equals(Object o){
        if (o instanceof WayPoint){
            WayPoint wp = (WayPoint)o;
            if (this.hashCode() == wp.hashCode()){
                return true;
            }
        }
        return false;
    }
}
