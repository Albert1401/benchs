//package solvers;
//
//import tasks.Task;
//
//import java.util.*;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//
//public class EARLSolverExEx extends AbstractEASolver implements EASolver {
//    final List<EASolverExplorer> explorers;
////    final List<EASolverManual> exploiters;
//    final double eps;
//    final int learningLimit;
//    final double eta;
//    final double discount;
//    final int exploitingLimit;
//    final int states;
//    final List<Map<Integer, Double>> QAS = new ArrayList<>();
//
//    public int getState(boolean x[]) {
//        return 0;
//    }
//
//    public int getState(double f, Task task) {
//        //TODO
//        int i = (int) ((task.fitnessIWant() + 5) / states);
//        return ((int) f) / i;
////        return 0;
//    }
//
//    Random random = new Random();
//
//    double getQ(int state, int solver) {
//        //TODO random [0, 1]?
////        QAS.get(solver).putIfAbsent(state, solver == 0 ? 0.5 : random.nextDouble() / 4);
////        QAS.get(solver).putIfAbsent(state, random.nextDouble() / 10);
//        QAS.get(solver).putIfAbsent(state, 0.0);
//        return QAS.get(solver).get(state);
//    }
//
//
//    double reward(Info stepInfo, double fgain) {
//        return fgain * fgain;
//    }
//
//
//    void updateQ(Info info, double fgain, int solver, int prevState, int nextState) {
//        double maxQ = IntStream.range(0, explorers.size())
//                .mapToDouble(i -> getQ(nextState, i))
//                .max().orElse(0);
//
////        maxQ = 0;
//        double prevQ = getQ(prevState, solver);
//        double nextQ = (1 - eta) * prevQ + eta * (reward(info, fgain) + discount * maxQ);
//
//        QAS.get(solver).put(prevState, nextQ);
//    }
////
//    int getExplorerGreedily(int state) {
//        int maxi = 0;
//        double max = Double.MIN_VALUE;
//        for (int si = 0; si < explorers.size(); si++) {
//            double x = getQ(state, si);
//            if (x > max) {
//                maxi = si;
//                max = x;
//            }
//        }
//        //TODO
//        if (getQ(state, Math.abs(1 - maxi)) == max){
//            return random.nextInt(2);
//        }
//        return maxi;
//    }
////
//    @Override
//    public Info solve(Task task) {
//        QAS.clear();
//        for (int i = 0; i < explorers.size(); i++) {
//            QAS.add(new HashMap<>());
//        }
//
//        int dim = task.dimension();
//        boolean[] x = init(dim);
//        double f = task.fitness(x);
//        int state = getState(f, task);
//
//        List<Info.EpochInfo> epochInfos = new ArrayList<>();
//        Random random = new Random();
//
//        int ep;
//        int fcalls = 0;
//        double p = 1.0 / task.dimension();
//        int lambda = 10;
//
//        for (ep = 1; fcalls <= fcallslimit && f < task.fitnessIWant(); ep++) {
//            //Learning strategy
//            int si;
//            Info info = null;
//            lambda = Math.min(60, lambda);
//            p = 1.0 / task.dimension();
//
//            if (random.nextDouble() < eps) {
//                //Explore
//                si = random.nextInt(explorers.size());
//            } else {
//                //Exploit
//                si = getExplorerGreedily(state);
//            }
//            info = explorers.get(si).solve(task, x, p, lambda, learningLimit);
//            info.epochInfos.stream().peek(i -> i.solver = -1).forEach(epochInfos::add);
//            fcalls += info.allCalls();
//
//            p = info.prob;
//            lambda = info.lambda;
//
////            double fnew = info.value();
//            double fnew = task.fitness(info.x);
//            if (fnew >= f) {
//                f = fnew;
//                x = info.x;
//            }
//
//            // Use
//            EASolverExplorer solver = new OneLambda(lambda, Integer.MAX_VALUE);
//            info = solver.solve(task, x, p, lambda, exploitingLimit);
//            info.epochInfos.stream().peek(i -> i.solver = -1).forEach(epochInfos::add);
//            fcalls += info.allCalls();
//
////            fnew = info.value();
//            fnew = task.fitness(info.x);
//            updateQ(info, fnew - f, si, state, state);
//
//            if (fnew >= f) {
//                f = fnew;
//                x = info.x;
//            }
//        }
//        return new Info(epochInfos.size(), epochInfos, x);
//    }
//
//    @Override
//    public String getName() {
//        return String.format("ExEx eps=%.2f learn=%d eta=%.2f " +
//                "disc=%.2f limit=%d fstates=%d", eps, learningLimit, eta, discount, exploitingLimit, states);
//    }
//
//    @Override
//    public EASolver copy() {
//        List<EASolverExplorer> ss = explorers.stream().map(s -> (EASolverExplorer) s.copy()).collect(Collectors.toList());
//        return new EARLSolverExEx(fcallslimit, ss, eps, learningLimit, eta, discount, exploitingLimit ,states);
//    }
//
//    public EARLSolverExEx(int fcallslimit, List<EASolverExplorer> explorers, double eps,
//                          int learningLimit, double eta, double discount, int exploitingLimit, int states) {
//        super(fcallslimit);
//        this.explorers = explorers.stream().map(e -> (EASolverExplorer) e.copy()).collect(Collectors.toList());
////        this.exploiters =exploiters;
//        this.eps = eps;
//        this.learningLimit = learningLimit;
//        this.eta = eta;
//        this.discount = discount;
//        this.exploitingLimit = exploitingLimit;
//        this.states = states;
//    }
//}
