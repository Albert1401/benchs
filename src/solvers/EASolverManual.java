package solvers;

import javafx.util.Pair;
import tasks.Task;

public interface EASolverManual extends EASolver {
    Info solve(Task task, boolean[] x, int fcallslimit);
}
