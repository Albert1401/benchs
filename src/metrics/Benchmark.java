package metrics;

import solvers.EASolver;
import solvers.Info;
import tasks.Task;

import java.util.*;

public class Benchmark {
    final EASolver solver;
    final Task task;
    final int i;

    public Benchmark(EASolver solver, Task task, int i) {
        this.solver = solver;
        this.task = task;
        this.i = i;
    }

    public void toDir(String dirPath){
        System.out.println("Running " + getName() + " benchmark...");
        Info info = solver.solve(task);
        info.toFile(dirPath + getPath(), getName());
        System.out.println(String.format("%dth iter of %s, %.2f value, %d epochs, %d fcallsavg",
                i + 1, getName(), info.value(), info.epochs, info.allCalls()));
    }

    public String getPath(){
        return solver.getName().hashCode() + "_" + task.getName().hashCode() + "_" + i;
    }

    public String getName() {
        return solver.getName() + '$' + task.getName() + '$' + i;
    }

    public static class A {
        public final double f;
        public final double fcallsavg;
        public final double fcallsstd;
        public final Map<Integer, Double> solversStat;

        public A(double f, double fcallsavg, double fcallsstd, Map<Integer, Double> solversStat) {
            this.f = f;
            this.fcallsstd = fcallsstd;
            this.fcallsavg = fcallsavg;
            this.solversStat = solversStat;
        }
    }
}
