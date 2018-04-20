package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class OneLambda extends AbstractEASolver implements EASolverManual {
    private final int lambda;

    AdaptiveGenSize.ProbType ptype;

    public OneLambda(AdaptiveGenSize.ProbType ptype, int lambda, int fcallslimit) {
        super(fcallslimit);
        this.ptype = ptype;
        this.lambda = lambda;
    }

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }


    @Override
    public String getName() {
        return "1+" + lambda + ":>0,p=" + ptype;
    }

    @Override
    public EASolver copy() {
        return new OneLambda(ptype, lambda, fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        int dim = task.dimension();
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
        if (f < task.fitnessIWant()){
            System.err.println("Can't reach fcalls");
        }
        return new Info(ep - 1, infos, x);
    }
}
