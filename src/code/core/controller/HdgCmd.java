package code.core.controller;

import code.enums.CommandType;

public class HdgCmd implements ATCCmd {
    public int assignTime;
    public double delta;
    public double targetHeading;

    public HdgCmd(int assignTime,double delta,double targetHeading){
        this.assignTime = assignTime;
        this.delta = delta;
        this.targetHeading = targetHeading;
    }

    @Override
    public CommandType getType() {
        return CommandType.Direction;
    }
    @Override
    public int getAssignTime() {
        return 0;
    }
    @Override
    public void setAssignTime(int time) {
        this.assignTime=time;
    }
    @Override
    public String toString(){
        return "HdgCmd: [time:" + assignTime + ",Delta:" + delta + ",Target:" + targetHeading + "]";
    }
}
