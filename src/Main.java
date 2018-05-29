import gg.GG;
import metrics.Benchmark;
import solvers.*;
import tasks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public void testSolversParallel(List<? extends EASolver> solvers, Function<Object, Task> taskg, String dir, int times) {
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

    static List<EARLSolverEx> forParams(int fcallslimit, List<EASolverExplorer> explorers, List<Double> epss,
                                        List<Integer> fsteps, List<Double> etas,
                                        List<Double> discounts, List<Integer> fstates, List<Integer> LPs, List<Integer> PPs) {
        return epss.stream().flatMap(eps ->
                fsteps.stream().flatMap(fstep ->
                        etas.stream().flatMap(eta ->
                                discounts.stream().flatMap(discount ->
                                        fstates.stream().flatMap(state ->
                                                LPs.stream().flatMap(LP ->
                                                        PPs.stream().map(PP -> {
                List<EASolverExplorer> ss = explorers.stream().map(s -> (EASolverExplorer) s.copy()).collect(Collectors.toList());
                return new EARLSolverEx(fcallslimit, ss, eps, fstep, eta, discount, state, LP, PP);
                                                                }
                                                        ))))))).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
        int fcallslimit = 100_000_000;

        EASolverExplorer solverMut = new AdaptiveMut(2, 40, fcallslimit, 2, 0.5);

        EASolverExplorer solverAdaptLambdaSOne = new AdaptiveGenSize(AdaptiveGenSize.SizeChangeType.ONE, 10, 1, 500, fcallslimit, -1, -1);
        EASolverExplorer solverAdaptLambdaAB = new AdaptiveGenSize(AdaptiveGenSize.SizeChangeType.AB, 40, 1, 500, fcallslimit, 2, 0.5);
        EASolverExplorer solverAdaptLambdaSS = new AdaptiveGenSize(AdaptiveGenSize.SizeChangeType.SUCCESSRATE, 10, 1, 500, fcallslimit, -1, -1);

        EASolverExplorer solver1 = new OneLambda(1, fcallslimit);
//        EASolverManual solver2 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 2, 1000000);
        EASolverExplorer solver5 = new OneLambda(5, fcallslimit);
//        EASolverManual solver10 = new OneLambda(AdaptiveGenSize.ProbType.STATIC, 10, fcallslimit);
        EASolverExplorer solver50 = new OneLambda(50, fcallslimit);

        List<EASolverExplorer> explorers = Arrays.asList(solverMut, solverAdaptLambdaAB, solverAdaptLambdaSOne, solver1);


        List<EASolver> solvers = Arrays.asList(solver1, solver5, solver50, solverMut, solverAdaptLambdaAB, solverAdaptLambdaSOne, solverAdaptLambdaSS);

        List<EASolver> all = new ArrayList<>();
        all.addAll(solvers);
        all.addAll(forParams(fcallslimit, explorers,
                Arrays.asList(0.01, 0.09),
                Arrays.asList(300, 800),
                Arrays.asList(0.05, 0.1),
                Arrays.asList(0.1),
                Arrays.asList(1),
                Arrays.asList(25, 50),
                Arrays.asList(2, 4)));

        Main main = new Main();
//        main.testLD(all);
//        main.testOM(all);
//        main.testMST(all);
        main.testTSP(all, args[0], args[1]);
    }

    int times = 50;

    void testLD(List<EASolver> solvers) {
        int dim = 2500;
        String outputDir = "LD_R/";
//        testSolversParallel(solvers, (x) -> new LeadingOnes(dim), outputDir, times);
        DrawHelper.main(new String[]{"LD_R/", "LD/", dim + ""});
    }

    void testOM(List<EASolver> solvers) {
        int dim = 20000;
        String outputDir = "OM_R/";
//        testSolversParallel(solvers, (x) -> new OneMax(dim), outputDir, times);
        DrawHelper.main(new String[]{"OM_R/", "OM/", dim + ""});
    }

    void testMST(List<EASolver> solvers) {
        int v = 81;
        String outputDir = "MST_R/";
        List<MST.Edge> gr = GG.generate0RDegRandom(v, 10);
//        testSolversParallel(solvers, (x) -> new MST(v * (v - 1), v, new ArrayList<>(gr)), outputDir, times);
        DrawHelper.main(new String[]{outputDir, "MST/", "20"});
    }

    void testTSP(List<EASolver> solvers, String tspFile, String tspOptFile){
        String outputDir = "TSP_R/";
//        testSolversParallel(solvers, (x) -> new TSP(tspFile, tspOptFile), outputDir, times);
        DrawHelper.main(new String[]{outputDir, "TSP/", "1"});
    }
}