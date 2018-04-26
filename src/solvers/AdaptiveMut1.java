package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdaptiveMut1 extends AbstractEASolver implements EASolverManual {

    final double initr;
    final int lambda;

    public AdaptiveMut1(double initr, int lambda, int fcallslimit) {
        super(fcallslimit);
        this.initr = initr;
        this.lambda = lambda;
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
        return new AdaptiveMut1(initr, lambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        double localr = initr;
        int dim = task.dimension();

        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;
        int fcalls = 0;
        Random random = new Random();
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            boolean[][] gen1 = generate(x, localr / 2 / dim, lambda / 2);
            boolean[][] gen2 = generate(x, localr * 2 / dim, lambda - lambda / 2);

            int fi = -1;
            double localf = -2_000_000_000;

            for (int j = 0; j < gen1.length; j++) {
                double fnew = task.fitness(gen1[j]);
                if (fnew >= localf) {
                    localf = fnew;
                    fi = j;
                }
            }
            boolean onFirst = true;
            for (int j = 0; j < gen2.length; j++) {
                double fnew = task.fitness(gen2[j]);
                if (fnew >= localf) {
                    localf = fnew;
                    fi = j;
                    onFirst = false;
                }
            }
            boolean succ = false;
            if (localf >= f){
                succ = true;
                f = localf;
                x = onFirst ? gen1[fi] : gen2[fi];
            }

            if (random.nextBoolean()) {
                localr *= random.nextBoolean() ? 0.5 : 2;
            } else {
                localr *= onFirst ? 0.5 : 2;
            }
            localr = Math.max(2, localr);
            localr = Math.min(dim / 4.0, localr);
            fcalls += lambda;
            infos.add(new Info.EpochInfo(f, lambda, succ));
        }
        return new Info(ep - 1, infos, x);
    }
}
