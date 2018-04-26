package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveGenSize extends AbstractEASolver implements EASolverExplorer {
    final int lambda0;
    final SizeChangeType ltype;

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }


    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        return solve(task, x, 1.0 / task.dimension(), lambda0, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, double p, int lambda, int fcallslimit) {
        double f = task.fitness(x);
        List<Info.EpochInfo> infos = new ArrayList<>();
        int ep;

        int fcalls = 0;
        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            boolean[][] gen = generate(x, p, lambda);

            double localf = f;
            int s = 0;
            for (boolean[] xnew : gen) {
                double fnew = task.fitness(xnew);
                if (fnew >= f) {
                    f = fnew;
                    x = xnew;
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
                lambda = Math.max(1, lambda);
            } else {
                lambda *= 2;
            }
        }
        return new Info(ep - 1, infos, x, lambda, p);
    }

    public enum ProbType {
        STATIC("n^-1"),
        LAMBDA("0.5*ln(initlambda)*n^-1"),;

        private final String repr;

        ProbType(String s) {
            repr = s;
        }

        @Override
        public String toString() {
            return repr;
        }
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


    public AdaptiveGenSize(int lambda0, SizeChangeType ltype, int fcallslimit) {
        super(fcallslimit);
        this.lambda0 = lambda0;
        this.ltype = ltype;
    }


    @Override
    public String getName() {
        return "1+" + ltype + ":>0";
    }

    @Override
    public EASolver copy() {
        return new AdaptiveGenSize(lambda0, ltype, fcallslimit);
    }
}
