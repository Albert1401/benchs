package tasks;


public class OneMax implements Task {
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
