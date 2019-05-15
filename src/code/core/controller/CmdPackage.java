package code.core.controller;

import code.core.aircraft.Aircraft;

public class CmdPackage {
    public Aircraft aircraft;
    public ATCCmd cmd;

    public CmdPackage(Aircraft aircraft, ATCCmd cmd){
        this.aircraft = aircraft;
        this.cmd = cmd;
    }
}
