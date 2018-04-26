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

//    public List<A> callsToReachValue(List<Info> infos) {
//        return callsToReachValue(infos, 0, task.fitnessIWant(), 300);
//    }
//
//    // fitness -> average fcalls to reach that value
//    public List<A> callsToReachValue(List<Info> infos, double start, double end, int n) {
//        double step = (end - start) / n;
//        System.out.println("Running " + getName() + " benchmark...");
//        int[][] f2calls = new int[n][infos.size()];
//
//
//        for (int i = 0; i < infos.size(); i++) {
//            Info info = infos.get(i);
//            System.out.println(String.format("%dth iter of %s, %.2f value, %d fcallslimit, %d fcalls",
//                    i + 1, getName(), info.value(), info.epochs, info.allCalls()));
//            int fcalls = 1;
//            int fprogress = 0;
//
//
//            for (int j = 0; fprogress < n && j < info.epochInfos.size(); ) {
//                double currentf = fprogress == n - 1 ? task.fitnessIWant() : step * (fprogress + 1);
//                Info.EpochInfo epoch = info.epochInfos.get(j);
//
//
//                if (epoch.fvalue < currentf) {
//                    fcalls += epoch.fcalls;
//                    j++;
//                } else {
//                    f2calls[fprogress][i] = fcalls + epoch.fcalls;
//                    fprogress += 1;
//                }
//            }
//
//        }
//        List<A> res = new ArrayList<>(n);
//        for (int fprogress = 0; fprogress < n; fprogress++) {
//            double currentf = fprogress == n - 1 ? task.fitnessIWant() : step * (fprogress + 1);
//            Arrays.stream(f2calls[fprogress])
//                    .forEach(d -> {
//                        if (d == 0) {
//                            System.err.println("Not reached desired fitness value: " + currentf);
//                        }
//                    });
//            double avgCalls = Arrays.stream(f2calls[fprogress])
//                    .filter(d -> d != 0)
//                    .average().orElse(0);
//
//            res.add(new A(currentf, avgCalls));
//        }
//        //TODO oh boy
//        System.gc();
//
//        return res;
//    }

    public void toDir(String dirPath){
        System.out.println("Running " + getName() + " benchmark...");
        Info info = solver.solve(task);
        info.toFile(dirPath + getPath(), getName());
        System.out.println(String.format("%dth iter of %s, %.2f value, %d epochs, %d fcalls",
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
        public final double fcalls;
        public final Map<Integer, Double> solversStat;

        public A(double f, double fcalls, Map<Integer, Double> solversStat) {
            this.f = f;
            this.fcalls = fcalls;
            this.solversStat = solversStat;
        }
    }
}
