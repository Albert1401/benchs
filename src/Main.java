import gg.GG;
import metrics.Benchmark;
import solvers.*;
import tasks.LeadingOnes;
import tasks.MST;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public void testSolversParallel(List<EASolver> solvers, Task task, String dir, int times) {
        IntStream.range(0, times).boxed()
                .flatMap(i -> solvers.stream().map(s -> new Benchmark(s.copy(), task, i)))
                .parallel()
                .forEach(b -> {
                    try {
                        b.toDir(dir);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void testSolversParallel(List<EASolver> solvers, Function<Object, Task> taskg, String dir, int times) {
        IntStream.range(0, times).boxed()
                .flatMap(i -> solvers.stream().map(s -> new Benchmark(s.copy(), taskg.apply(new Object()), i)))
                .parallel()
                .forEach(b -> {
                    try {
                        b.toDir(dir);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }


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

    void cvEARL(String outDir, int times, int epochs, List<EASolverManual> solvers, List<Double> epss,
                List<Integer> fsteps, List<Double> etas, List<Double> discounts, List<Integer> states) {
        List<EARLSolver> earlSolverStream = epss.stream().flatMap(eps ->
                fsteps.stream().flatMap(fstep ->
                        etas.stream().flatMap(eta ->
                                discounts.stream().flatMap(discount ->
                                        states.stream().map(state -> new EARLSolver(epochs, solvers, eps, fstep, eta, discount, state)))))).collect(Collectors.toList());
        earlSolverStream.stream().parallel().forEach(solver -> {
            Task task = new LeadingOnes(1500);
            testSolversParallel(Arrays.asList(solver), task, outDir, times);
        });
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, Vladislav Kamenev");
        int fcallslimit = 2000000;
        EASolverManual solver1R = new AdaptiveMut(2, 50, fcallslimit);
//        EASolverManual solverAB = new AdaptiveABMut(1.2, 0.85, 0.1, 20_000_000);

        EASolverManual solverAdaptLambdaSOne = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.STATIC, AdaptiveGenSize.SizeChangeType.STATICONE, fcallslimit);
        EASolverManual solverAdaptLambdaSTwo = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.STATIC, AdaptiveGenSize.SizeChangeType.STATICTWO, fcallslimit);
//        EASolverManual solverAdaptLambdaSS = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.STATIC, AdaptiveGenSize.SizeChangeType.SUCCESSRATE, 1000000);
//        EASolverManual solverAdaptLambdaDOne = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.LAMBDA, AdaptiveGenSize.SizeChangeType.STATICONE, 1000000);
//        EASolverManual solverAdaptLambdaDTwo = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.LAMBDA, AdaptiveGenSize.SizeChangeType.STATICTWO, 1000000);
//        EASolverManual solverAdaptLambdaDS = new AdaptiveGenSize(10, AdaptiveGenSize.ProbType.LAMBDA, AdaptiveGenSize.SizeChangeType.SUCCESSRATE, 1000000);

        EASolverManual solverRLS = new RLS(20_000_000);
        EASolverManual solver1 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 1, fcallslimit);
//        EASolverManual solver2 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 2, 1000000);
//        EASolverManual solver5 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 5, 1000000);
        EASolverManual solver10 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 10, fcallslimit);
//        EASolverManual solver50 = new OneLambda(AdaptiveGenSize.ProbType.LAMBDA, 50, 1000000);


//        List<EASolverManual> solvers = Arrays.asList(solver1R, solverAB, solverAdaptLambdaSOne, solverAdaptLambdaSTwo,
//                solverAdaptLambdaSS, solverAdaptLambdaDOne, solverAdaptLambdaDTwo, solverAdaptLambdaDS,
//                solverRLS, solver1, solver2, solver5, solver10, solver50);

        List<EASolverManual> solvers = Arrays.asList(solverRLS, solverAdaptLambdaSOne, solverAdaptLambdaSTwo,
                solver1, solver10);


        EASolver earl1 = new EARLSolver(fcallslimit, solvers, 0.1, 50, 0.1, 0.6, 1);
//        EASolver earl2 = new EARLSolver(500000, solvers, 0.1, 100, 0.1, 0.6, 4);
        Main main = new Main();
//        main.cvEARL("EARL/", 5, 500000, solvers, Arrays.asList(0.1, 0.2, 0.05),
//                Arrays.asList(50, 150, 400),
//                Arrays.asList(0.1, 0.2, 0.6),
//                Arrays.asList(0.1, 0.4, 0.7),
//                Arrays.asList(1, 4, 20));

//        Files.createDirectory(Paths.get("EARL2/"));
//        main.cvEARL("EARL2/", 3, 500000, solvers,
//                Arrays.asList(0.1),
//                Arrays.asList(200),
//                Arrays.asList(0.1),
//                Arrays.asList(0.3),
//                Arrays.asList(1, 4));
        List<EASolver> solvers1 = solvers.stream().map(EASolver::copy).collect(Collectors.toList());
        solvers1.clear();
        solvers1.add(earl1);
        solvers1.add(solver1);
//        int dim = Integer.parseInt(args[0]);
//        String outputDir = args[1];
//        int times = Integer.parseInt(args[2]);
//
        int v = 41;
        int dim = (v * (v - 1)) / 2;
        String outputDir = "benchsMST/";
        int times = 3;
//
//

        List<MST.Edge> gr = GG.generate0RFull(v);
        main.testSolversParallel(solvers1, (x) -> new MST(dim, v, new ArrayList<>(gr)), outputDir, times);
//        main.testSolversParallel(Collections.singletonList(earl), new LeadingOnes(dim), outputDir, times);
//        main.testSolversParallel(solvers, new OneMax(dim), outputDir, times);

//        List<EASolverManual> solvers = Arrays.asList(solver1R);

//        long st = System.currentTimeMillis();
//        main.testSolversParallel(solvers, new LeadingOnes(ar), args[0], 6);
//        long end = System.currentTimeMillis();
//        System.out.println(end - st);
    }
}
