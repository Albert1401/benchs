package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static solvers.AdaptiveGenSize.ProbType.LAMBDA;

public class OneLambda extends AbstractEASolver implements EASolverManual {
    private final int lambda;
    private final double p;
    public OneLambda(int lambda, double p, int fcallslimit) {
        super(fcallslimit);
        this.lambda = lambda;
        this.p = p;
    }

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }


    @Override
    public String getName() {
        return String.format("1+%d:>0p=.2%f", lambda, p);
    }

    @Override
    public EASolver copy() {
        return new OneLambda(lambda, p, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;
        int fcalls = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            boolean[][] gen = generate(x, p, lambda);


            boolean succ = false;
            for (boolean[] xnew : gen) {
                double fnew = task.fitness(xnew);
                if (fnew >= f) {
                    f = fnew;
                    x = xnew;
                    succ = true;
                }
            }
            fcalls += lambda;
            infos.add(new Info.EpochInfo(f, lambda, succ));
        }
        return new Info(ep - 1, infos, x);
    }
}
