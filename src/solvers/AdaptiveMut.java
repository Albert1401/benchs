package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdaptiveMut extends AbstractEASolver implements EASolverExplorer {

    final double initr;
    final int initlambda;
    final double A, B;

    public AdaptiveMut(double initr, int initlambda, int fcallslimit, double A, double B) {
        super(fcallslimit);
        this.initr = initr;
        this.initlambda = initlambda;
        this.A = A;
        this.B = B;

    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task) {
        return solve(task, task.init(), fcallslimit);
    }

    @Override
    public String getName() {
        return "1+{2r;0.5r}:>0";
    }

    @Override
    public EASolver copy() {
        return new AdaptiveMut(initr, initlambda, fcallslimit, A , B);
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, int fcallslimit) {
        return solve(task, x, initr / task.dimension(), initlambda, fcallslimit);
    }

    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, double p, int lambda, int fcallslimit) {
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

        List<R> gen1 = task.initInds(lambda / 2);
        List<R> gen2 = task.initInds(lambda / 2);


        int lambdaStep = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {

            task.generate(A * r / dim, gen1);
            task.generate(B * r / dim, gen2);

            int fi1 = -1;
            int fi2 = -1;

            double fleft = f - 1;
            double fold = f;
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

                task.mutate(x, onFirst ? gen1.get(fi) : gen2.get(fi));
                r *= onFirst ? A : B;

                r = Math.max(2, r);
                r = Math.min(dim / 4.0, r);

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
        return new Info<>(ep - 1, infos, x, pl, r / task.dimension());
    }

    @Override
    public State.Type pType() {
        return State.Type.WITHOUTMUT;
    }
}
