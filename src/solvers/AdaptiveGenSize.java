package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveGenSize extends AbstractEASolver implements EASolverExplorer {
    final SizeChangeType ltype;
    final int initLambda;
    final int maxLambda;
    final int minLambda;
    final double A, B;

    @Override
    public <T, R> Info<T> solve(Task<T, R> task) {
        return solve(task, task.init(), fcallslimit);
    }


    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, int fcallslimit) {
        return solve(task, x, 1.0 / task.dimension(), initLambda, fcallslimit);
    }



    @Override
    public <T, R> Info<T> solve(Task<T, R> task, T x, double p, int lambda, int fcallslimit) {
        double f = task.fitness(x);
        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;

        int fcalls = 0;
        int lambdaStep = 0;
        List<R> gen = task.initInds(maxLambda);

        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            List<R> curGen = gen.subList(0, lambda);
            task.generate(p, curGen);

            double fold = f;
            int s = 0;
            R inds = null;

            for (R mutInds : curGen) {
                double fnew = task.fitness(x, mutInds, fold);

                if (fnew >= f) {
                    f = fnew;
                    inds = mutInds;
                }

                if (fnew >= fold) {
                    s += 1;
                }
            }
            fcalls += lambda;
            lambdaStep += lambda;
            if (f > fold){
                infos.add(new Info.EpochInfo(f, lambdaStep));
                lambdaStep = 0;
            }

            if (s != 0) {
                switch (ltype) {
                    case ONE:
                        lambda = 1;
                        break;
                    case AB:
                        lambda = (int) (lambda * B);
                        break;
                    case SUCCESSRATE:
                        lambda = lambda / s;
                        break;
                    default:
                        throw new RuntimeException("Unreachable");
                }
            } else {
                if (ltype == SizeChangeType.AB){
                    lambda = (int) (lambda * A);
                } else {
                    lambda *= 2;
                }
            }

            lambda = Math.min(maxLambda, lambda);
            lambda = Math.max(minLambda, lambda);

            if (inds != null) {
                task.mutate(x, inds);
            }
        }
        if (lambdaStep != 0){
            infos.add(new Info.EpochInfo(f, lambdaStep));
        }
        return new Info<>(ep - 1, infos, x, lambda, p);
    }

    @Override
    public State.Type pType() {
        return State.Type.WITHOUTLAMBDA;
    }

    public enum SizeChangeType {
        SUCCESSRATE("{2l;s^-1xl}"),
        ONE("{2l;1}"),
        AB("{A;B}");

        private final String repr;

        SizeChangeType(String s) {
            repr = s;
        }

        @Override
        public String toString() {
            return repr;
        }
    }


    public AdaptiveGenSize(SizeChangeType ltype, int initLambda, int minLambda, int maxLambda, int fcallslimit, double A, double B) {
        super(fcallslimit);
        this.ltype = ltype;
        this.initLambda = initLambda;
        this.maxLambda = maxLambda;
        this.minLambda = minLambda;
        this.A = A;
        this.B = B;
    }

    @Override
    public String getName() {
        return "1+" + ltype + ":>0";
    }

    @Override
    public EASolver copy() {
        return new AdaptiveGenSize(ltype, initLambda, minLambda, maxLambda, fcallslimit, A, B);
    }
}
