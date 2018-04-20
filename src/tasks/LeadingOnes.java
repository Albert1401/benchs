package tasks;

public class LeadingOnes implements Task {
    public int n;

    public LeadingOnes(int n) {
        this.n = n;
    }

    @Override
    public double fitness(boolean[] x) {
        for (int i = 0; i < x.length; i++) {
            if (!x[i]) {
                return i;
            }
        }
        return x.length;
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
        return "LeadingOnes_" + n;
    }
}
