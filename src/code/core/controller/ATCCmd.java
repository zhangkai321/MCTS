package code.core.controller;

import code.enums.CommandType;

public interface ATCCmd {
    CommandType getType();
    int getAssignTime();
    void setAssignTime(int time);
}
