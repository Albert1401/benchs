package solvers;

import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class EARLSolverEx extends AbstractEASolver implements EASolver {
    final List<EASolverExplorer> solvers;
    final double eps;
    final int fstep;
    final double eta;
    final double discount;
    final int fstates;

    double f, fmax, p;
    int lambda, dim;
    final int LP, PP;

    //action -> state -> measure
    final List<Map<State, Double>> QAS = new ArrayList<>();

    public State getState(int si) {
        return new State(f, fstates, fmax, p, lambda, LP, PP, dim, solvers.get(si).pType());
    }

    Random random = new Random();

    double getQ(int solver) {
        State state = getState(solver);
        return getQ(state, solver);
    }

    double getQ(State state, int solver) {
        QAS.get(solver).putIfAbsent(state, 0.0);
        return QAS.get(solver).get(state);
    }

    double reward(double fgain) {
        return fgain == 0 ? -0.1 : fgain;
    }

    void updateQ(double fgain, int solver, State state) {
        double nextStepQ = IntStream.range(0, solvers.size())
                .mapToDouble(this::getQ)
                .max().orElse(0);

        double prevQ = getQ(state, solver);
        double nextQ = (1 - eta) * prevQ + eta * (reward(fgain) + discount * nextStepQ);
        QAS.get(solver).put(state, nextQ);
    }

    int getSolverRandomly() {
        State.Type[] types = State.Type.values();
        State.Type rType = types[random.nextInt(types.length)];

        return IntStream.range(0, solvers.size()).boxed()
                .filter(si -> solvers.get(si).pType() == rType)
                .max((s1, s2) -> 2 * random.nextInt(2) - 1)
                .get();
    }

    double[] qs;

    int getSolverGreedily() {
        for (int si = 0; si < solvers.size(); si++) {
            qs[si] = getQ(si);
        }
        double maxQ = Arrays.stream(qs).max().orElse(0);
        return IntStream.range(0, solvers.size())
                .filter(i -> qs[i] == maxQ)
                .boxed()
                .max((s1, s2) -> 2 * random.nextInt(2) - 1)
                .get();
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task) {
        dim = task.dimension();
        int ep;
        State state;
        int fcalls = 0;
        T x = task.init();
        List<Info.EpochInfo> epochInfos = new ArrayList<>();
        QAS.clear();
        for (int i = 0; i < solvers.size(); i++) {
            QAS.add(new HashMap<>());
        }
        qs = new double[solvers.size()];

        f = task.fitness(x);
        p = 1.0 / task.dimension();
        lambda = 1;
        fmax = task.fitnessIWant();


        for (ep = 1; fcalls + fstep <= fcallslimit && f < task.fitnessIWant(); ep++) {

            //Strategy
            int si;
            if (random.nextDouble() < eps) {
                //Explore
                si = getSolverRandomly();
            } else {
                //Exploit
                si = getSolverGreedily();
            }

            //Step
            EASolverExplorer solver = solvers.get(si);
            Info<T> info = solver.solve(task, x, p, lambda, fstep);
            info.epochInfos.stream().peek(i -> i.solver = si).forEach(epochInfos::add);

            //Remember state before changing it
            state = getState(si);

            //Changing state
            double fnew = info.value();
            double fgain = fnew - f;
            if (fnew >= f) {
                f = fnew;
                x = info.x;
            }
            if (fnew > f){
                p = info.prob;
                lambda = Math.min(info.lambda, fstep);
            }


            fcalls += info.allCalls();
            //Update Q
            updateQ(fgain, si, state);
        }
        return new Info<>(epochInfos.size(), epochInfos, x);
    }

    @Override
    public String getName() {
        return String.format("Ex eps=%.2f fstep=%d eta=%.2f " +
                "disc=%.2f fstates=%d LP=%d PP=%d", eps, fstep, eta, discount, fstates, LP, PP);
    }

    @Override
    public EASolver copy() {
        List<EASolverExplorer> ss = solvers.stream().map(s -> (EASolverExplorer) s.copy()).collect(Collectors.toList());
        return new EARLSolverEx(fcallslimit, ss, eps, fstep, eta, discount, fstates, LP, PP);
    }

    public EARLSolverEx(int fcallslimit, List<EASolverExplorer> solvers, double eps,
                        int fstep, double eta, double discount, int fstates, int LP, int PP) {
        super(fcallslimit);
        this.solvers = solvers;
        this.eps = eps;
        this.fstep = fstep;
        this.eta = eta;
        this.discount = discount;
        this.fstates = fstates;
        this.LP = LP;
        this.PP = PP;
    }
}
