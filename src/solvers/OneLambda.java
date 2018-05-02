package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class OneLambda extends AbstractEASolver implements EASolverExplorer {
    private final int lambda0;
    private final double p0;
    public OneLambda(int lambda0, double p0, int fcallslimit) {
        super(fcallslimit);
        this.lambda0 = lambda0;
        this.p0 = p0;
    }

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }


    @Override
    public String getName() {
        return String.format("1+%d:>0p=%f", lambda0, p0);
    }

    @Override
    public EASolver copy() {
        return new OneLambda(lambda0, p0, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        return solve(task, x, p0, lambda0, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit) {
        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;
        int fcalls = 0;
        List<int[]> gen = initInds(task.dimension(), lambda);

        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            generate(x, p, gen);
            int[] indx = null;

            boolean succ = false;
            double flocal = f;

            for (int[] inds : gen) {
                double fnew = task.fitness(x, inds, flocal);
                if (fnew >= f) {
                    f = fnew;
                    indx = inds;
                    succ = true;
                }
            }

            fcalls += lambda;
            if (indx != null){
                mutate(x, indx);
            }
            infos.add(new Info.EpochInfo(f, lambda, succ));
        }
        return new Info(ep - 1, infos, x, lambda, p);
    }
}