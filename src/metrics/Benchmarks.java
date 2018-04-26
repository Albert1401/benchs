package metrics;

import solvers.Info;

import java.util.*;

public class Benchmarks {
    public static List<Benchmark.A> callsToReachValue(List<Info> infos, double r, boolean calcUnsucc) {
        double st = infos.stream().flatMap(l -> l.epochInfos.stream()).mapToDouble(ei -> ei.fvalue).min().orElse(0);
        return callsToReachValue(infos, st, r, 300, calcUnsucc);
    }

    // fitness -> average fcalls to reach that value
    public static List<Benchmark.A> callsToReachValue(List<Info> infos, double start, double end, int n, boolean calcUnsucc) {
        double step = (end - start) / n;
        int[][] f2calls = new int[n][infos.size()];

        List<Map<Integer, Double>> f2solvers = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            f2solvers.add(new HashMap<>());
        }
        for (int i = 0; i < infos.size(); i++) {
            Info info = infos.get(i);
            int fcalls = 1;
            int fprogress = 0;

            for (int j = 0; fprogress < n && j < info.epochInfos.size(); ) {
                double currentf = fprogress == n - 1 ? end : start + step * (fprogress + 1);
                Info.EpochInfo epoch = info.epochInfos.get(j);

                Map<Integer, Double> sols = f2solvers.get(fprogress);

                sols.putIfAbsent(epoch.solver, 0.0);
                sols.compute(epoch.solver, (k, v) -> v + 1.0 / infos.size());

                if (epoch.fvalue < currentf) {
                    if (epoch.success || calcUnsucc) {
                        fcalls += epoch.fcalls;
                    }
                    j++;
                } else {
                    f2calls[fprogress][i] = fcalls + epoch.fcalls;
                    fprogress += 1;
                }
            }

        }
        //fix solvs map
        int mins = f2solvers.stream().flatMap(ss -> ss.keySet().stream()).mapToInt(i -> i).min().orElse(0);
        int maxs = f2solvers.stream().flatMap(ss -> ss.keySet().stream()).mapToInt(i -> i).max().orElse(0);
        f2solvers.forEach(ss -> {
            for (int i = mins; i <= maxs; i++) {
                ss.putIfAbsent(i, 0.0);
            }
        });

        List<Benchmark.A> res = new ArrayList<>(n);
        for (int fprogress = 0; fprogress < n; fprogress++) {
            double currentf = fprogress == n - 1 ? end : start + step * (fprogress + 1);
            Arrays.stream(f2calls[fprogress])
                    .forEach(d -> {
                        if (d == 0) {
                            System.err.println(infos.get(0).getName() + " not reached desired fitness value: " + currentf);
                        }
                    });
            double avgCalls = Arrays.stream(f2calls[fprogress])
                    .filter(d -> d != 0)
                    .average().orElse(0);

            res.add(new Benchmark.A(currentf, avgCalls, f2solvers.get(fprogress)));
        }
        return res;
    }
}
