package code.core.controller;

import code.enums.CommandType;

public class SpdCmd implements ATCCmd {
    public int assignTime;
    public double delta;
    public double targetTAS;

    public SpdCmd(int assignTime, double delta, double targetTAS) {
        this.assignTime = assignTime;
        this.delta = delta;
        this.targetTAS = targetTAS;
    }

    @Override
    public CommandType getType() {
        return CommandType.Speed;
    }
    @Override
    public int getAssignTime() {
        return assignTime;
    }
    @Override
    public void setAssignTime(int time) {
        this.assignTime = time;
    }
    @Override
    public String toString(){
        return "SpdCmd: [time:" + assignTime + ",delta:" + delta + ",target: " + targetTAS +"]";
    }
}
