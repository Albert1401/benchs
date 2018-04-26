package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveMut extends AbstractEASolver implements EASolverManual, EASolverExplorer {

    final double initr;
    final int initlambda;

    public AdaptiveMut(double initr, int initlambda, int fcallslimit) {
        super(fcallslimit);
        this.initr = initr;
        this.initlambda = initlambda;
    }

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }

    @Override
    public String getName() {
        return "1+{2*r;0.5*r}:>0";
    }

    @Override
    public EASolver copy() {
        return new AdaptiveMut(initr, initlambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        return solve(task, x, initr / task.dimension(), initlambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit) {
        int dim = task.dimension();
        lambda = 50;
        double r = p * task.dimension();
        r = Math.max(2, r);
        r = Math.min(dim / 4.0, r);

        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;
        int fcalls = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            boolean[][] gen1 = generate(x, r / 2 / dim, lambda / 2);
            boolean[][] gen2 = generate(x, r * 2 / dim, lambda - lambda / 2);

            int fi = -1;

            for (int j = 0; j < gen1.length; j++) {
                double fnew = task.fitness(gen1[j]);
                if (fnew >= f) {
                    f = fnew;
                    fi = j;
                }
            }
            boolean onFirst = true;
            for (int j = 0; j < gen2.length; j++) {
                double fnew = task.fitness(gen2[j]);
                if (fnew >= f) {
                    f = fnew;
                    fi = j;
                    onFirst = false;
                }
            }
            if (fi != -1){
                x = onFirst ? gen1[fi] : gen2[fi];
                r *= onFirst ? 0.5 : 2;

                r = Math.max(2, r);
                r = Math.min(dim / 4.0, r);
            }

            fcalls += lambda;
            infos.add(new Info.EpochInfo(f, lambda, fi != 1));
        }
        return new Info(ep - 1, infos, x, lambda, r / task.dimension());
    }
}
