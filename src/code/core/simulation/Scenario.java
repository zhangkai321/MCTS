package code.core.simulation;

import code.core.aircraft.Aircraft;
import code.core.aircraft.FlightPlan;
import code.core.controller.Controller;
import code.enums.FlightStage;
import code.enums.Result;
import code.util.DataAccess;
import code.vision.Visualization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Scenario {
    public static Clock clock = new Clock();
    public static Aircraft A;
    public static Aircraft B;
    public Controller controller;
    DataAccess access=new DataAccess();
    public static Map<String, WayPoint> AllPoints;
    public static Map<String, FlightPlan> FPMap;
    public static List<String> colors;

    /** 构造函数 **/
    public Scenario() {
        AllPoints =access.allNaipPtMap;
        FPMap=access.flightPlanMap;
        colors= new ArrayList<>(Arrays.asList("FF00FF00","FF0000FF","FF9F5F9F","FFC0C0C0"));
    }

    /** Main函数 **/
    public void run() throws IOException {
        deadWork();
        simulation();
        follow_up();
    }

    /*todo: 1*/
    private void deadWork(){
        LoadAircraft();
        LoadController();
        clock.setStartTime(Math.min(A.curFltPlan.startTime,B.curFltPlan.startTime)-1);
    }
    private void LoadAircraft(){
        String path1="#FP#387,NCA254,B744,72,#FP#451,AIQ765,A320,21";

        String[] a=path1.split(",");

        FlightPlan flightPlan1=FPMap.get(a[0]);
        flightPlan1.startTime=Integer.parseInt(a[3]);
        FlightPlan flightPlan2=FPMap.get(a[4]);
        flightPlan2.startTime=Integer.parseInt(a[7]);

        A=flightPlan1.aircraft;
        A.stage= FlightStage.NotStarted;
        B=flightPlan2.aircraft;
        B.stage= FlightStage.NotStarted;

        A.curFltPlan=flightPlan1;
        B.curFltPlan=flightPlan2;

    }
    private void LoadController(){
        controller=new Controller();
    }

    /*todo: 2*/
    private void simulation(){
        int a=0;
        while(!A.isFinished() && !B.isFinished()){
            clock.doStep();
            A.doStep();
            B.doStep();
            controller.doStep();
//            if (controller.conflict!=null && controller.tree.result!= Result.Solved){
//                System.err.println("-------------------------------------------------------\n" +
//                                   "--There is conflict, but solution searching is "+controller.tree.result+"--\n"+
//                                   "-------------------------------------------------------");
//                return;
//            }
            a++;
        }
        System.out.println("The minimum distance: "+controller.minimum_distance);
        System.out.println("The time: "+controller.minimum_time);
    }

    /*todo: 3*/
    public void follow_up() throws IOException {
        File f1=new File("src/code/vision/1");
        if (f1.exists() && f1.isDirectory()){
            deleteFile(f1.getAbsolutePath());
        }
        else {
            f1.mkdir();
        }

        new Visualization(f1,A);
        new Visualization(f1,B);
    }
    private void deleteFile(String path){
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
        new Scenario().run();
    }
}
