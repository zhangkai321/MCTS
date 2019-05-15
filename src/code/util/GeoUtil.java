package code.util;


import code.core.simulation.WayPoint;

/**
 * 地理计算工具类。
 * 原来的代码似乎计算不是特别精确
 * 现在采用新的算法，详见doc里面的GeoUtil.pdf
 */
public class GeoUtil {
    public static double fixAngle(double angle){
        if(angle > 180){
            return angle - 360;
        }
        if(angle < -180){
            return angle + 360;
        }
        return angle;
    }

    public static double calIntersectionAngle(double h1, double h2){
        double ret = h1-h2;
        return ret>180?ret-360:(ret<-180?ret+360:ret);
    }

    public static double calDist(double lng1, double lat1, double lng2, double lat2){
        lng1*=Global.Deg2Rad;
        lng2*=Global.Deg2Rad;
        lat1*=Global.Deg2Rad;
        lat2*=Global.Deg2Rad;
        double x = Math.sin((lat2-lat1)/2.0);
        x = x*x;
        double y = Math.sin((lng2-lng1)/2.0);
        y = y*y;
        double tmp = x+Math.cos(lat1)*Math.cos(lat2)*y;
        return Math.asin(Math.sqrt(tmp))*2*Global.EarthRadius * 1000;
    }
    public static double calDist(WayPoint start, WayPoint end){
        return calDist(start.lng, start.lat,end.lng,end.lat);
    }

    public static double calHeading(double lng1, double lat1, double lng2, double lat2){
        lng1*=Global.Deg2Rad;
        lng2*=Global.Deg2Rad;
        lat1*=Global.Deg2Rad;
        lat2*=Global.Deg2Rad;
        double dLng = lng2-lng1;
        double cosLat2 = Math.cos(lat2);
        double y = Math.sin(dLng)*cosLat2;
        double x = Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*cosLat2*Math.cos(dLng);
        return Math.atan2(y, x)*Global.Rad2Deg;
    }
    public static double calHeading(WayPoint start, WayPoint end){
        return calHeading(start.lng, start.lat, end.lng, end.lat);
    }

    public static WayPoint getDestination(WayPoint source, double heading, double distance){
        WayPoint ret = new WayPoint();
        double[] coord = calDestPt(source.lng,source.lat,heading,distance);
        ret.lng = coord[0];
        ret.lat = coord[1];
        return ret;
    }

    public static double[] calDestPt(double lng, double lat, double heading, double dist){
        double rLng1 = lng*Global.Deg2Rad;
        double rLat1 = lat*Global.Deg2Rad;
        double r = (dist * 0.001)/Global.EarthRadius;
        heading*=Global.Deg2Rad;
        double cosR = Math.cos(r);
        double sinR = Math.sin(r);
        double sinLat1 = Math.sin(rLat1);
        double cosLat1 = Math.cos(rLat1);

        double rLat2 = Math.asin(sinLat1*cosR+cosLat1*sinR*Math.cos(heading));
        double rLng2 = rLng1+Math.atan2(Math.sin(heading)*sinR*cosLat1, cosR-sinLat1*Math.sin(rLat2));
        return new double[]{rLng2*Global.Rad2Deg, rLat2*Global.Rad2Deg};
    }

    @Deprecated
    public static double calDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = lat1*Math.PI/180;
        double radLat2 = lat2*Math.PI/180;
        double tmpRadian = Math.sin(radLat1)*Math.sin(radLat2)
                +Math.cos(radLat1)*Math.cos(radLat2)*Math.cos((lon2-lon1)*Math.PI/180);
        return Math.acos(tmpRadian)*Global.EarthRadius;
    } //大量使用Cos，有可能会造成较大误差
    @Deprecated
    public static double[] calCoordinate(double lon, double lat, double heading, double dist){
        double theta=0;  //连线和X轴的夹角
        int coef=1;
        int coef2=1;
        if(heading<0){
            heading+=360;
        }
        if(heading>360){
            heading+=360;
        }
        if(heading<90){
            theta=90-heading;
            coef=1;
        }else if(heading<180){
            theta=heading-90;
            coef=-1;
        }else if(heading<270){
            theta=270-heading;
            coef=-1;
            coef2=-1;
        }else{
            theta=heading-270;
            coef=1;
            coef2=-1;
        }
        double retLat =  lat + coef * dist * Math.sin(theta*Math.PI/180)/Global.KMPerLat;
        double retRadLat = retLat*Math.PI/180;
        double radLat = lat*Math.PI/180;
        double tmp = Math.cos(dist/Global.NM2KM/60/180*Math.PI);
        double longDev = (tmp - Math.sin(radLat) * Math.sin(retRadLat))/
                (Math.cos(radLat)*Math.cos(retRadLat));
        if (longDev > 1) {
            longDev = 1;
        }
        double retLon = lon + coef2*Math.acos(longDev)*180/Math.PI;
        return new double[]{retLon, retLat};
    }
}
