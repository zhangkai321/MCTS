package code.util;


import code.core.aircraft.Aircraft;
import code.core.aircraft.FlightPlan;
import code.core.simulation.Routing;
import code.core.simulation.WayPoint;
import kt.mas.core.traffic.aircraft.AircraftType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataAccess {
    public static Map<String, WayPoint> allNaipPtMap;
    public static Map<String, FlightPlan> flightPlanMap;
    public static Map<String,Routing> allRoutingMap;
    public static Map<List<String>,List<WayPoint>> sectorMap;

    String key = "MySQL";
    String table;
    Random random=new Random();
    public DataAccess() {
        allNaipPtMap = getAllNaipPtMap();
        flightPlanMap=getFlightPlanMap();
        allRoutingMap=getAllRoutingMap();
        sectorMap=getSectorMap();
    }

    /** 航路点 **/
    private Map<String,WayPoint> getAllNaipPtMap() {
        table = "allpoint_naip";
        Map<String, WayPoint> allPtMap = new HashMap<>();
        ResultSet rs = JDBCHelper.getResultSet(table, key);
        try {
            while (rs.next()) {
                String pt = rs.getString("fix_pt");
                double lat = rs.getDouble("latitude");
                double lng = rs.getDouble("longitude");
                WayPoint point=new WayPoint(pt,lng,lat);
                allPtMap.put(pt,point);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allPtMap;
    }

    /** 飞行计划 **/
    private Map<String,FlightPlan> getFlightPlanMap(){
        table="plan20180601_test";
        Map<String,FlightPlan> allFlightPlan=new HashMap<>();
        ResultSet rs = JDBCHelper.getResultSet(table, key);

        int i=0;
        try {
            while (rs.next()) {
                String id="#FP#"+i;
                String callSign=rs.getString("flt_no");
                String type=rs.getString("acft_type");
                String path=rs.getString("flt_path");

                Aircraft aircraft=LoadAircraft(callSign,type);
                Routing routing=LoadRouting(path);

                FlightPlan flightPlan=new FlightPlan(id,aircraft,routing,LoadRFL(),LoadStartTime());

                if (aircraft!=null && routing.getSegments().size()>1){
                    allFlightPlan.put(id,flightPlan);
                    i++;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return allFlightPlan;
    }
    private Aircraft LoadAircraft(String callSign,String type){
        AircraftType aircraftType=AircraftType.Companion.getALL().get(type);
        if (aircraftType!=null){
            return new Aircraft(callSign,aircraftType);
        }
        return null;
    }
    private Routing LoadRouting(String path){
        ArrayList<WayPoint> points = new ArrayList<>();
        List<String> realPath=new ArrayList<>();
        for (String point_id: path.split("-")){
            WayPoint p =allNaipPtMap.get(point_id) ;
            if (p != null) {
                realPath.add(point_id);
                points.add(p);
            }
        }
        return new Routing(String.join("-",realPath), points);
    }
    private double LoadRFL(){
        return 9000.0;
//        return 6000.0+300.0*random.nextInt(21);
    }
    private int LoadStartTime(){
        return random.nextInt(120);
    }

    /** 国内航路 **/
    private Map<String,Routing> getAllRoutingMap(){
        table = "route_naip";
        Map<String,Routing> routingMap=new HashMap<>();
        Map<String,Map<Integer,WayPoint>> temp = new HashMap<>();
        ResultSet rs = JDBCHelper.getResultSet(table, key);
        try {
            while (rs.next()) {
                String pt=rs.getString("fix_pt");
                double lng=rs.getDouble("longitude");
                double lat=rs.getDouble("latitude");
                String name=rs.getString("route");
                int seq=rs.getInt("seq");

                WayPoint point=new WayPoint(pt,lng,lat);
                if (temp.containsKey(name)){
                    temp.get(name).put(seq,point);
                }
                else {
                    Map<Integer,WayPoint> pointMap=new HashMap<>();
                    pointMap.put(seq,point);
                    temp.put(name,pointMap);
                }
            }
            for (String name:temp.keySet()){
               Object[] objects= temp.get(name).keySet().toArray();
               Arrays.sort(objects);
               List<WayPoint> list=new ArrayList<>();
               for (Object o:objects){
                   list.add(temp.get(name).get(o));
               }
                routingMap.put(name,new Routing(name,list));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routingMap;
    }

    /** 国内扇区 **/
    private Map<List<String>,List<WayPoint>> getSectorMap(){
        table = "sector_2018";
        Map<List<String>,Map<Integer,WayPoint>> temp=new HashMap<>();
        Map<List<String>,List<WayPoint>> sectorMap=new HashMap<>();
        ResultSet rs = JDBCHelper.getResultSet(table, key);
        try {
            while (rs.next()) {
                String artcc=rs.getString("artcc");
                String area=rs.getString("area");
                String name=rs.getString("name");
                int seq=rs.getInt("seq");
                double lng=rs.getDouble("longitude");
                double lat=rs.getDouble("latitude");
                double alt=rs.getDouble("Height_Cons");
                List<String> tempList=Arrays.asList(artcc,area,name);
                WayPoint point=new WayPoint(lng,lat,alt);
                if (temp.containsKey(tempList)){
                    temp.get(tempList).put(seq,point);
                }
                else {
                    Map<Integer,WayPoint> pointList=new HashMap<>();
                    pointList.put(seq,point);
                    temp.put(tempList,pointList);
                }
            }
            for (List<String> strings:temp.keySet()){
                Object[] objects= temp.get(strings).keySet().toArray();
                Arrays.sort(objects);
                List<WayPoint> list=new ArrayList<>();
                for (Object o:objects){
                    list.add(temp.get(strings).get(o));
                }
                sectorMap.put(strings,list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sectorMap;
    }
}
