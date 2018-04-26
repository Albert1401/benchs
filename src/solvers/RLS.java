package solvers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RLS extends AbstractEASolver implements EASolverManual {

    public RLS(int fcallslimit) {
        super(fcallslimit);
    }

    @Override
    public Info solve(Task task) {
        return solve(task, init(task.dimension()), fcallslimit);
    }

    @Override
    public String getName() {
        return "RLS";
    }

    @Override
    public EASolver copy() {
        return new RLS(fcallslimit);
    }

    @Override
    public Info solve(Task task, boolean[] x, int fcallslimit) {
        double f = task.fitness(x);

        List<Info.EpochInfo> infos = new ArrayList<>();

        int ep;
        Random random = new Random();
        for (ep = 1; ep <= fcallslimit && f < task.fitnessIWant(); ep++) {
            int k = random.nextBoolean() ? 1 : 2;

            boolean[] xnew = x.clone();
            mutate(xnew, k);

            double fnew = task.fitness(xnew);
            boolean succ = false;
            if (fnew >= f) {
                x = xnew;
                f = fnew;
                succ = true;
            }
            infos.add(new Info.EpochInfo(f, 1, succ));
        }
        return new Info(ep - 1, infos, x);
    }
}
