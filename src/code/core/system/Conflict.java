package code.core.system;

import code.core.aircraft.Aircraft;
import code.core.aircraft.State;
import code.enums.LateralType;

public class Conflict {
    public double distance;
    public State state1;
    public State state2;
    public Aircraft acft1;
    public Aircraft acft2;
    public LateralType lateralType;

    public Conflict(State state1, State state2, LateralType type, double distance){
        this.state1 = state1;
        this.state2 = state2;
        this.lateralType = type;
        this.distance = distance;
        acft1 = state1.aircraft;
        acft2 = state2.aircraft;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Conflict){
            Conflict conflict = (Conflict)obj;
            if ((conflict.state1.equals(this.state1)&& conflict.state2.equals(this.state2))
                    ||(conflict.state1.equals(this.state2)&& conflict.state2.equals(this.state1))){
                if (conflict.lateralType.equals(this.lateralType) && conflict.distance == this.distance){
                    return true;
                }
            }
        }
        return false;
    }
    public String toString(){
        return "@conflict information: \n"+
            "------------------------------------\n" +
            "two guys: "+acft1.id+","+acft2.id+"\n"+
            "lateral type: "+lateralType + "\n" +
            "distance: "+distance/1000.0+"KM"+"\n"+
            "------------------------------------\n";
    }
    public int hashCode(){
        return state1.hashCode() + state2.hashCode();
    }
}

