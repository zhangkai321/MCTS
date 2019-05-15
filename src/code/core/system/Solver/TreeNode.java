package code.core.system.Solver;

import code.core.aircraft.State;
import code.core.controller.CmdPackage;

import java.util.List;
import java.util.Random;

public interface TreeNode {
    SolverTree getTree();
    int getHeight();
    TreeNode getParent();
    int getPlayCount();
    void setPlayCount(int count);
    int getOkCount();
    void setOkCount(int count);
    State getState1();
    State getState2();
    List<? extends TreeNode> getChildren();
    boolean isAllChildrenExpanded();
    Random rd = new Random();
}


