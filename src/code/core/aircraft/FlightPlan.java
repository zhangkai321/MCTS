package code.core.aircraft;

import code.core.simulation.Routing;

public class FlightPlan {
    public String id;
    public Aircraft aircraft;
    public Routing routing;
    public double RFL;
    public int startTime;

    public FlightPlan(String id, Aircraft aircraft, Routing routing, double RFL, int startTime){
        this.id = id;
        this.aircraft = aircraft;
        this.routing = routing;
        this.RFL = RFL;
        this.startTime = startTime;
    }

    public String toString(){
        return id +"::"+ routing + " RFL:" + RFL + "startTime: " + startTime + " " + aircraft.type;
    }
}
