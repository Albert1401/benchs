package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class OneLambda extends AbstractEASolver implements EASolverExplorer {
    private final int lambda0;
    public OneLambda(int lambda0, int fcallslimit) {
        super(fcallslimit);
        this.lambda0 = lambda0;
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task) {
        return solve(task, task.init(), fcallslimit);
    }


    @Override
    public String getName() {
        return String.format("1+%d:>0", lambda0);
    }

    @Override
    public EASolver copy() {
        return new OneLambda(lambda0, fcallslimit);
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, int fcallslimit) {
        double p = 1.0 / task.dimension();
        return solve(task, x, p, lambda0, fcallslimit);
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, double p, int lambda, int fcallslimit) {
        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;
        int fcalls = 0;
        List<R> gen = task.initInds(lambda);
        int lambdaStep = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            task.generate(p, gen);
            R indx = null;

            double fold = f;

            for (R inds : gen) {
                double fnew = task.fitness(x, inds, fold);
//                mutate(x, inds);
//                if (task.fitness(x) != fnew){
//                    System.out.println("asd");
//                    task.fitness(x);
//                    mutate(x, inds);
//                    task.fitness(x, inds, fold);
//                }
                if (fnew >= f) {
                    f = fnew;
                    indx = inds;
                }
            }

            if (indx != null){
                task.mutate(x, indx);
            }
            fcalls += lambda;
            lambdaStep += lambda;
            if (f > fold){
                infos.add(new Info.EpochInfo(f, lambdaStep));
                lambdaStep = 0;
            }
        }
        if (lambdaStep != 0){
            infos.add(new Info.EpochInfo(f, lambdaStep));
        }
        return new Info<>(ep - 1, infos, x, lambda, p);
    }

    @Override
    public State.Type pType() {
        return State.Type.ALL;
    }
}