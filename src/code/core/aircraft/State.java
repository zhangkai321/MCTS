package code.core.aircraft;

import code.core.controller.HdgCmd;
import code.core.simulation.WayPoint;
import demo.spatialindex.rtree.Rect;
import demo.spatialindex.rtree.SpatialData;
import kt.mas.core.traffic.aircraft.FlightPerformance;
import code.core.controller.AltCmd;
import code.core.controller.SpdCmd;
import code.enums.VerticalState;

public class State implements SpatialData {
    public Aircraft aircraft;
    public int time;
    public double heading;
    public double alt;
    public double vSpd;
    public double hSpdTAS;
    public double turnRate;
    public boolean finished = false;

    public AltCmd altCmd;
    public SpdCmd spdCmd;
    public HdgCmd hdgCmd;
    public WayPoint location;
    public FlightPlan fltPlan;
    public VerticalState vState;
    public Position pos;
    public FlightPerformance performance;

    public State(Aircraft aircraft){this.aircraft = aircraft;}

    public String toString(){
        return "State: "+aircraft.id + "," +
               "Time: "+time+","+
               "Heading:"+heading+","+
               "Location: "+ location + "," +
               "Altitude: "+alt + ","+
               "Speed:" + hSpdTAS;
    }
    public Rect getBounds() {
        return new Rect(location.lng, location.lng, location.lat, location.lat, alt, alt);
    }
}
