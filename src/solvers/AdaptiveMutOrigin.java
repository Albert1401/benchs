package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdaptiveMutOrigin extends AbstractEASolver implements EASolverManual {

    final double initr;
    final int lambda;

    public AdaptiveMutOrigin(double initr, int lambda, int fcallslimit) {
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
        return new AdaptiveMutOrigin(initr, lambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit) {
        int dim = task.dimension();
        int pl = lambda;

        lambda = Math.max(initlambda, lambda);
        double r = p * task.dimension();
        r = Math.max(2, r);
        r = Math.min(dim / 4.0, r);

        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;
        int fcalls = 0;
        Random random = new Random();

        List<int[]> gen1 = initInds(dim, lambda / 2);
        List<int[]> gen2 = initInds(dim, lambda / 2);

        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {

            generate(x, r / 2 / dim, gen1);
            generate(x, r * 2 / dim, gen2);

            int fi1 = -1;
            int fi2 = -1;

            double fleft = f - 1;
            for (int j = 0; j < gen1.size(); j++) {
                double fnew = task.fitness(x, gen1.get(j), f);
                if (fnew >= fleft) {
                    fleft = fnew;
                    fi1 = j;
                }
            }

            double fright = f - 1;
            for (int j = 0; j < gen2.size(); j++) {
                double fnew = task.fitness(x, gen2.get(j), f);
                if (fnew >= fright) {
                    fright = fnew;
                    fi2 = j;
                }
            }

            if (Math.max(fleft, fright) >= f) {
                f = Math.max(fleft, fright);
                int fi;
                boolean onFirst;

                if (fleft == fright) {
                    onFirst = random.nextBoolean();
                    fi = onFirst ? fi1 : fi2;
                } else {
                    onFirst = fleft > fright;
                    fi = onFirst ? fi1 : fi2;
                }

                mutate(x, onFirst ? gen1.get(fi) : gen2.get(fi));
                r *= onFirst ? 0.5 : 2;

                r = Math.max(2, r);
                r = Math.min(dim / 4.0, r);

                infos.add(new Info.EpochInfo(f, lambda, true));
            } else {
                infos.add(new Info.EpochInfo(f, lambda, false));
            }
            fcalls += lambda;
        }
        return new Info(ep - 1, infos, x, pl, r / task.dimension());
    }}
