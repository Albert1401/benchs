import metrics.Benchmark;
import solvers.*;
import tasks.LeadingOnes;
import tasks.Task;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public void testSolversParallel(List<? extends EASolver> solvers, Task task, String dir, int times) {
        IntStream.range(0, times).boxed()
                .flatMap(i -> solvers.stream().map(s -> new Benchmark(s.copy(), task, i)))
//                .parallel()
                .forEach(b -> {
                    try {
                        b.toDir(dir);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

//    public void testSolversParallel(List<EASolver> solvers, Function<Object, Task> taskg, String dir, int times) {
//        IntStream.range(0, times).boxed()
//                .flatMap(i -> solvers.stream().map(s -> new Benchmark(s.copy(), taskg.apply(new Object()), i)))
//                .parallel()
//                .forEach(b -> {
//                    try {
//                        b.toDir(dir);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//    }


//    public static void benchmarkToFile(String dir, Benchmark benchmark, int times) {
//        List<Benchmark.A> res = benchmark.callsToReachValue(times);
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(dir + benchmark.getName()))) {
//            writer.write(res.size() + "");
//            writer.newLine();
//
//            for (Benchmark.A r : res) {
//                writer.write(r.f + "");
//                writer.newLine();
//                writer.write(r.fcalls + "");
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    void cvEARL(String outDir, int times, int fcallslimit, List<EASolverManual> solvers, List<Double> epss,
//                List<Integer> fsteps, List<Double> etas, List<Double> discounts, List<Integer> states) {
//        List<EARLSolver> earlSolverStream = epss.stream().flatMap(eps ->
//                fsteps.stream().flatMap(fstep ->
//                        etas.stream().flatMap(eta ->
//                                discounts.stream().flatMap(discount ->
//                                        states.stream().map(state -> new EARLSolver(fcallslimit, solvers, eps, fstep, eta, discount, state)))))).collect(Collectors.toList());
//        earlSolverStream.stream()
//                .parallel()
//                .forEach(solver -> {
//                    Task task = new LeadingOnes(1500);
//                    testSolversParallel(Arrays.asList(solver), task, outDir, times);
//                });
//    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, Sof");
//        System.console().readLine();
        int fcallslimit = 2_000_000;

        EASolverExplorer solver1R = new AdaptiveMut(2, 50, fcallslimit);
//        EASolverManual solverAB = new AdaptiveABMut(1.2, 0.85, 0.1, fcallslimit);

        EASolverExplorer solverAdaptLambdaSOne = new AdaptiveGenSize(10, AdaptiveGenSize.SizeChangeType.STATICONE, fcallslimit);
        EASolverExplorer solverAdaptLambdaSTwo = new AdaptiveGenSize(10, AdaptiveGenSize.SizeChangeType.STATICTWO, fcallslimit);
        EASolverExplorer solverAdaptLambdaSS = new AdaptiveGenSize(10, AdaptiveGenSize.SizeChangeType.SUCCESSRATE, fcallslimit);

        EASolverManual solverRLS = new RLS(fcallslimit);
        EASolverManual solver1 = new OneLambda(1, 1.0 / 1500, fcallslimit);
//        EASolverManual solver2 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 2, 1000000);
        EASolverManual solver5 = new OneLambda(5, 1.0 / 1500, fcallslimit);
//        EASolverManual solver10 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 10, fcallslimit);
        EASolverManual solver50 = new OneLambda(50, 1.0 / 1500, fcallslimit);

//        List<EASolverManual> solvers = Arrays.asList(solver1R);
        List<EASolverExplorer> explorers = Arrays.asList(solver1R, solverAdaptLambdaSTwo);
        EASolver earl1 = new EARLSolver11(fcallslimit, explorers, 0.1, 100, 0, 0, 0);


//        int dim = Integer.parseInt(args[0]);
//        String outputDir = args[1];
//        int times = Integer.parseInt(args[2]);
        int dim = 1500;
        String outputDir = "222/";
        int times = 5;
        Main main = new Main();
        main.testSolversParallel(Arrays.asList(earl1), new LeadingOnes(dim), outputDir, times);

//        EASolver earl2 = new EARLSolver(fcallslimit, solvers, 0.15, 250, 0.1, 0.7, 1);


//        List<MST.Edge> gr = GG.generate0RFull(v);
//        main.testSolversParallel(solvers1, (x) -> new MST(dim, v, new ArrayList<>(gr)), outputDir, times);
//        main.testSolversParallel(Arrays.asList(earl1,earl2,earl3), new LeadingOnes(dim), outputDir, times);
//        main.testSolversParallel(solvers, new OneMax(dim), outputDir, times);
    }
}
