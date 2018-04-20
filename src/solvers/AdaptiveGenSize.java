package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveGenSize extends AbstractEASolver implements EASolverManual {
    final int lambda0;
    final ProbType ptype;
    final SizeChangeType ltype;

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        int dim = task.dimension();
        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;

        int lambda = lambda0;
        int fcalls = 0;

        for (ep = 1; fcalls + lambda <= fcallslimit && f < task.fitnessIWant(); ep++) {
            double p;
            switch (ptype) {
                case LAMBDA:
                    p = Math.log(lambda) / 2 / dim;
                    break;
                case STATIC:
                    p = 1.0 / dim;
                    break;
                default:
                    throw new RuntimeException("Unreachable");
            }
            p = Math.max(1.0 / dim / dim, p);
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
        if (f < task.fitnessIWant()){
            System.err.println("Can't reach fcalls");
        }
        return new Info(ep - 1, infos, x);
    }

    public enum ProbType {
        STATIC("n^-1"),
        LAMBDA("0.5*ln(lambda)*n^-1"),;

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


    public AdaptiveGenSize(int lambda0, ProbType ptype, SizeChangeType ltype, int fcallslimit) {
        super(fcallslimit);
        this.lambda0 = lambda0;
        this.ptype = ptype;
        this.ltype = ltype;
    }


    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }

    @Override
    public String getName() {
        return "1+" + ltype + ":>0,p=" + ptype;
    }

    @Override
    public EASolver copy() {
        return new AdaptiveGenSize(lambda0, ptype, ltype, fcallslimit);
    }
}
