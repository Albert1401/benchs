package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveGenSize extends AbstractEASolver implements EASolverExplorer {
    final SizeChangeType ltype;
    final int initLambda;
    final int maxLambda;
    final int minLambda;

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }


    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        return solve(task, x, 1.0 / task.dimension(), initLambda, fcallslimit);
    }



    @Override
    public Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit) {
        double f = task.fitness(x);
        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;

        int fcalls = 0;
        List<int[]> gen = initInds(task.dimension(), maxLambda);

        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            List<int[]> curGen = gen.subList(0, lambda);
            generate(x, p, curGen);

            double localf = f;
            int s = 0;
            int[] inds = null;

            for (int[] mutInds : curGen) {
                double fnew = task.fitness(x, mutInds, localf);

                if (fnew >= f) {
                    f = fnew;
                    inds = mutInds;
                }

                if (fnew >= localf) {
                    s += 1;
                }
            }

            infos.add(new Info.EpochInfo(f, lambda, s != 0));
            fcalls += lambda;
            if (s != 0) {
                switch (ltype) {
                    case STATICONE:
                        lambda = 1;
                        break;
                    case STATICTWO:
                        lambda = lambda / 2;
                        break;
                    case SUCCESSRATE:
                        lambda = lambda / s;
                        break;
                    default:
                        throw new RuntimeException("Unreachable");
                }
            } else {
                lambda *= 2;
            }

            lambda = Math.min(maxLambda, lambda);
            lambda = Math.max(minLambda, lambda);

            if (inds != null) {
                mutate(x, inds);
            }
        }
        return new Info(ep - 1, infos, x, lambda, p);
    }

    public enum SizeChangeType {
        SUCCESSRATE("{2l;s^-1*l}"),
        STATICONE("{2l;1}"),
        STATICTWO("{2l;0.5*l}");

        private final String repr;

        SizeChangeType(String s) {
            repr = s;
        }

        @Override
        public String toString() {
            return repr;
        }
    }


    public AdaptiveGenSize(SizeChangeType ltype, int initLambda, int minLambda, int maxLambda, int fcallslimit) {
        super(fcallslimit);
        this.ltype = ltype;
        this.initLambda = initLambda;
        this.maxLambda = maxLambda;
        this.minLambda = minLambda;
    }

    @Override
    public String getName() {
        return "1+" + ltype + ":>0";
    }

    @Override
    public EASolver copy() {
        return new AdaptiveGenSize(ltype, initLambda, minLambda, maxLambda, fcallslimit);
    }
}
