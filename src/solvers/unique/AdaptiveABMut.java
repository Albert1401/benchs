//package solvers;
//
//import tasks.Task;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class AdaptiveABMut extends AbstractEASolver {
//    final double A;
//    final double B;
//    final double p0;
//
//    public AdaptiveABMut(double a, double b, double p0, int fcallslimit) {
//        super(fcallslimit);
//        A = a;
//        B = b;
//        this.p0 = p0;
//    }
//
//    @Override
//    public Info solve(Task task) {
//        int dim = task.dimension();
//        double p = p0;
//
//        boolean[] x = init(dim);
//        double f = task.fitness(x);
//
//        List<Info.EpochInfo> infos = new ArrayList<>();
//
//        int ep;
//
//        for (ep = 1; ep <= fcallslimit && f < task.fitnessIWant(); ep++) {
//            boolean[] xnew = generate(x, p, 1)[0];
//            double fnew = task.fitness(xnew);
//            boolean succ = false;
//            if (fnew >= f){
//                p = Math.min(1.0 / 2, p * A);
//                x = xnew;
//                f = fnew;
//                succ = true;
//            } else {
//                p = Math.max(1.0 / dim / dim, p * B);
//            }
//            infos.add(new Info.EpochInfo(f, 1, succ));
//        }
//        return new Info(ep - 1, infos, x);
//    }
//
//    @Override
//    public String getName() {
//        return "1+{A*p;B*p}:>0";
//    }
//
//    @Override
//    public EASolver copy() {
//        return new AdaptiveABMut(A, B, p0, fcallslimit);
//    }
//
//}
