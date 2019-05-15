package code.core.simulation;

public class Clock {
    private int startTime;
    private int time;
    private int timeStep;

    public Clock(){
         this.startTime = 0;
         timeStep = 1;
     }

    public void doStep() {
        time += timeStep;
        System.out.println("Time " + time+"(Clock_doStep)");
    }

    public int getStartTime(){
        return startTime;
    }
    public void setStartTime(int startTime){
         this.startTime = startTime;
         time = startTime;
         System.out.println("Start Time : " + startTime+"(Clock_setStartTime)");
     }
    public int getTimeStep() {
        return timeStep;
    }
    public int getTime() {
        return time;
    }
}
