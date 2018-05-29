package tasks;


import java.util.List;

public class OneMax extends BTask {
    public int n;

    public OneMax(int n) {
        this.n = n;
    }

    @Override
    public double fitness(boolean[] x) {
        double res = 0;
        for (boolean b : x) {
            res += b ? 1 : 0;
        }
        return res;
    }

    @Override
    public double fitness(boolean[] x, int[] inds, double f) {
        for (int i = 1; i <= inds[0]; i++) {
            f += x[inds[i]] ? -1 : 1;
        }
//        if (f != fitness(x)){
//            System.err.println("Error");
//        }
        return f;
    }

    @Override
    public int dimension() {
        return n;
    }

    @Override
    public double fitnessIWant() {
        return n;
    }

    @Override
    public String getName() {
        return "OneMax_" + n;
    }
}
