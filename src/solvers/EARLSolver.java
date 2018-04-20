package solvers;

import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class EARLSolver extends AbstractEASolver implements EASolver {
    List<EASolverManual> solvers;
    final double eps;
    final int fstep;
    final double eta;
    final double discount;
    final int states;

    final List<Map<Integer, Double>> QAS = new ArrayList<>();

    public int getState(boolean x[]) {
        return 0;
    }

    public int getState(double f, Task task) {
//        int i = (task.fitnessIWant() + 5) / states;
//        return ((int) f) / i;
        return 0;
    }

    Random random = new Random();

    double getQ(int state, int solver) {
        //TODO random [0, 1]?
        QAS.get(solver).putIfAbsent(state, random.nextDouble());
        return QAS.get(solver).get(state);
    }


    double reward(Info stepInfo, double fgain) {
        return fgain;
    }


    void updateQ(Info info, double fgain, int solver, int prevState, int nextState) {
        double maxQ = IntStream.range(0, solvers.size())
                .mapToDouble(i -> getQ(nextState, i))
                .max().orElse(0);

        maxQ = 0;
        double prevQ = getQ(prevState, solver);
        double nextQ = (1 - eta) * prevQ + eta * (reward(info, fgain) + discount * maxQ);

        QAS.get(solver).put(prevState, nextQ);
    }

    int getSolverGreedily(int state) {
        int maxi = 0;
        double max = Double.MIN_VALUE;
        for (int si = 0; si < solvers.size(); si++) {
            double x = getQ(state, si);
            if (x > max) {
                maxi = si;
                max = x;
            }
        }
        return maxi;
    }

    @Override
    public Info solve(Task task) {
        QAS.clear();
        for (int i = 0; i < solvers.size(); i++) {
            QAS.add(new HashMap<>());
        }

        int dim = task.dimension();
        boolean[] x = init(dim);
        double f = task.fitness(x);
        int state = getState(f, task);


        List<Info.EpochInfo> epochInfos = new ArrayList<>();

        Random random = new Random();
        int ep;
        int fcalls = 0;
        for (ep = 1; fcalls + fstep <= fcallslimit; ep++) {

            //Strategy
            int si;
            if (random.nextDouble() < eps) {
                //Explore
                si = random.nextInt(solvers.size());
            } else {
                //Exploit
                si = getSolverGreedily(state);
            }

            //Step
            EASolverManual solver = solvers.get(si);
            Info info = solver.solve(task, x, fstep);
            epochInfos.addAll(info.epochInfos);

            //Changing state
            double fnew = task.fitness(info.x);
            double fgain = fnew - f;
            if (fnew >= f) {
                f = fnew;
                x = info.x;
            }
            int prevState = state;
            state = getState(f, task);


            fcalls += info.allCalls();
            //Update Q
            updateQ(info, fgain, si, prevState, state);
        }
        return new Info(epochInfos.size(), epochInfos, x);
    }

    @Override
    public String getName() {
        return String.format("EARL eps=%.2f fstep=%d eta=%.2f " +
                "disc=%.2f states=%d", eps, fstep, eta, discount, states);
    }

    @Override
    public EASolver copy() {
        List<EASolverManual> ss = solvers.stream().map(s -> (EASolverManual) s.copy()).collect(Collectors.toList());
        return new EARLSolver(fcallslimit, ss, eps, fstep, eta, discount, states);
    }

    public EARLSolver(int fcallslimit, List<EASolverManual> solvers, double eps,
                      int fstep, double eta, double discount, int states) {
        super(fcallslimit);
        this.solvers = solvers;
        this.eps = eps;
        this.fstep = fstep;
        this.eta = eta;
        this.discount = discount;
        this.states = states;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%.2f", 1.0 / 3));
    }
}
