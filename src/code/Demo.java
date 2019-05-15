package code;

import code.core.aircraft.Aircraft;
import code.core.aircraft.FlightPlan;
import code.core.simulation.Clock;
import code.core.simulation.Routing;
import code.core.simulation.WayPoint;
import code.util.DataAccess;
import code.util.GeoUtil;
import code.vision.Visualization;
import kt.mas.core.traffic.aircraft.AircraftType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Demo {
    public static Map<String, WayPoint> AllPoints;
    public static Clock clock=new Clock();
    public static Aircraft aircraft;

    public Demo(){
        this.aircraft=LoadAC();
        clock.setStartTime(-1*clock.getTimeStep());
    }
    public void run() throws IOException {
        int a=0;
        while (!aircraft.isFinished()){
            clock.doStep();
            aircraft.doStep();
            a++;
        }
        follow_up(aircraft);
    }
    public Aircraft LoadAC(){
        new DataAccess();
        AllPoints=DataAccess.allNaipPtMap;

        String path="NOBEM-XOGAX-VEMEX-SUNBO-LEBIK-LEGIV-HFE-TUTKI-MAGLI-NOKUL-AKAMI";
        Aircraft aircraft=new Aircraft("A", AircraftType.Companion.getALL().get("A320"));
        FlightPlan flightPlan=new FlightPlan("A",aircraft,LoadRoute(AllPoints,path),9000,0);
        aircraft.curFltPlan=flightPlan;
        return aircraft;
    }

    public Routing LoadRoute(Map<String,WayPoint> allPoints,String path){
        String[] temp=path.split("-");
        List<WayPoint> wayPoints=new ArrayList<>();
        for (String string:temp){
            WayPoint wayPoint=allPoints.get(string);
            wayPoints.add(wayPoint);
        }
        return new Routing(wayPoints);
    }
    public void follow_up(Aircraft aircraft) throws IOException {
        File f1=new File("src/vi");
        if (f1.exists() && f1.isDirectory()){
            deleteFile(f1.getAbsolutePath());
        }
        else {
            f1.mkdir();
        }

        new Visualization(f1,aircraft);
    }
    public void deleteFile(String path){
        File file=new File(path);
        String[] content=file.list();
        for (String string:content){
            File temp=new File(path,string);
            if (temp.isDirectory()){
                deleteFile(temp.getAbsolutePath());
                temp.delete();
            }
            else {
                temp.delete();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        new Demo().run();
    }
}
