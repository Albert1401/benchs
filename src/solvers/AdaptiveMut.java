package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveMut extends AbstractEASolver implements EASolverManual {

    final double initr;
    final int lambda;

    public AdaptiveMut(double initr, int lambda, int fcallslimit) {
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
        return new AdaptiveMut(initr, lambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        double localr = initr;
        int dim = task.dimension();

        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;
        int fcalls = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            boolean[][] gen1 = generate(x, localr / 2 / dim, lambda / 2);
            boolean[][] gen2 = generate(x, localr * 2 / dim, lambda - lambda / 2);

            int fi = -1;

            for (int j = 0; j < gen1.length; j++) {
                double fnew = task.fitness(gen1[j]);
                if (fnew > f) {
                    f = fnew;
                    fi = j;
                }
            }
            boolean onFirst = true;
            for (int j = 0; j < gen2.length; j++) {
                double fnew = task.fitness(gen2[j]);
                if (fnew > f) {
                    f = fnew;
                    fi = j;
                    onFirst = false;
                }
            }
            if (fi != -1) {
                if (onFirst) {
                    localr = Math.max(2, localr / 2);
                    x = gen1[fi];
                } else {
                    localr = Math.min(dim / 4.0, localr * 2);
                    x = gen2[fi];
                }
            }
            fcalls += lambda;
            infos.add(new Info.EpochInfo(f, lambda, fi != -1));
        }

        if (f < task.fitnessIWant()){
            System.err.println("Can't reach fcalls");
        }
        return new Info(ep - 1, infos, x);
    }
}
