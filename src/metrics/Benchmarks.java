package metrics;

import solvers.Info;
import solvers.unique.Time;

import java.util.*;
import java.util.stream.Collectors;

public class Benchmarks {
    public static List<Benchmark.A> callsToReachValue(List<Info<?>> infos, double r) {
        double st = infos.stream().flatMap(l -> l.epochInfos.stream()).mapToDouble(ei -> ei.fvalue).min().orElse(0);
        return callsToReachValue(infos, st, r, 600);
    }

    // fitness -> average fcallsavg to reach that value
    public static List<Benchmark.A> callsToReachValue(List<Info<?>> infos, double start, double end, int n) {
        List<Info<?>> nInfos = infos.stream()
                .peek(inf -> {
                    if (inf.value() < end){
                        System.err.println(inf.getName() + " not reached desired fitness value: " + inf.value());
                    }
                }).filter(i -> i.value() == end).collect(Collectors.toList());

        System.out.println("Was: " + infos.size());
        System.out.println("New: " + nInfos.size());
        double step = (end - start) / n;
        int[][] f2calls = new int[n][nInfos.size()];

        List<Map<Integer, Double>> f2solvers = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            f2solvers.add(new HashMap<>());
        }
        Time time = new Time();
        for (int i = 0; i < nInfos.size(); i++) {
            Info<?> info = nInfos.get(i);
            int fcalls = 1;
            int fprogress = 0;

            for (int j = 0; fprogress < n && j < info.epochInfos.size(); ) {
                double currentf = fprogress == n - 1 ? end : start + step * (fprogress + 1);
                Info.EpochInfo epoch = info.epochInfos.get(j);

                Map<Integer, Double> sols = f2solvers.get(fprogress);
//
                sols.putIfAbsent(epoch.solver, 0.0);
                sols.compute(epoch.solver, (k, v) -> v + 1.0 / nInfos.size());

                if (epoch.fvalue < currentf) {
                    fcalls += epoch.fcalls;
                    j++;
                } else {
                    f2calls[fprogress][i] = fcalls + epoch.fcalls;
                    fprogress += 1;
                }
            }

        }
        time.measure("aggregate");
        //fix solvs map
        int mins = f2solvers.stream().flatMap(ss -> ss.keySet().stream()).mapToInt(i -> i).min().orElse(0);
        int maxs = f2solvers.stream().flatMap(ss -> ss.keySet().stream()).mapToInt(i -> i).max().orElse(0);
        f2solvers.forEach(ss -> {
            for (int i = mins; i <= maxs; i++) {
                ss.putIfAbsent(i, 0.0);
            }
        });

        time.start();
        List<Benchmark.A> res = new ArrayList<>(n);
        for (int fprogress = 0; fprogress < n; fprogress++) {
            double currentf = fprogress == n - 1 ? end : start + step * (fprogress + 1);
            Arrays.stream(f2calls[fprogress])
                    .forEach(d -> {
                        if (d == 0) {
                            System.err.println(nInfos.get(0).getName() + " not reached desired fitness value: " + currentf);
                        }
                    });
            double avg = Arrays.stream(f2calls[fprogress])
                    .peek(d -> {
                        if (d == 0){
                            System.err.println(" == 0 !!");
                        }
                    })
                    .filter(d -> d != 0)
                    .average().orElse(0);
            double std = Math.pow(Arrays.stream(f2calls[fprogress])
                    .mapToDouble(x -> Math.pow(avg - x, 2))
                    .sum() / nInfos.size(), 0.5);

            res.add(new Benchmark.A(currentf, avg, std, f2solvers.get(fprogress)));
        }
        time.measure("endd");
        return res;
    }
}
