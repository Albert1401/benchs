package solvers;

import tasks.Task;

public interface EASolverExplorer extends EASolverManual{
    Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit);
}
