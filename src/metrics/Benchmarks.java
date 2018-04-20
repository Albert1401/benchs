package metrics;

import solvers.Info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Benchmarks {
    public static List<Benchmark.A> callsToReachValue(List<Info> infos, double r, boolean calcUnsucc) {
        double st = infos.stream().flatMap(l -> l.epochInfos.stream()).mapToDouble(ei -> ei.fvalue).min().orElse(0);
        return callsToReachValue(infos, st, r, 300, calcUnsucc);
    }

    // fitness -> average fcalls to reach that value
    public static List<Benchmark.A> callsToReachValue(List<Info> infos, double start, double end, int n, boolean calcUnsucc) {
        double step = (end - start) / n;
        int[][] f2calls = new int[n][infos.size()];
        for (int i = 0; i < infos.size(); i++) {
            Info info = infos.get(i);
            int fcalls = 1;
            int fprogress = 0;


            for (int j = 0; fprogress < n && j < info.epochInfos.size(); ) {
                double currentf = fprogress == n - 1 ? end : start + step * (fprogress + 1);
                Info.EpochInfo epoch = info.epochInfos.get(j);

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

            res.add(new Benchmark.A(currentf, avgCalls));
        }
        return res;
    }


}
