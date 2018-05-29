package tasks;

import solvers.AbstractEASolver;

import java.util.List;

public class XDivK extends BTask{
    private int n, k;
    private OneMax oneMax;

    public XDivK(int n, int k) {
        this.n = n;
        this.k = k;
        oneMax = new OneMax(n);
    }

    @Override
    public double fitness(boolean[] x) {
        return ((int) oneMax.fitness(x)) / k;
    }

    @Override
    public double fitness(boolean[] x, int[] inds, double f) {
        mutate(x, inds);
        double res = ((int) oneMax.fitness(x)) / k;
        rev(x, inds);
        return res;
    }

    @Override
    public int dimension() {
        return n;
    }

    @Override
    public double fitnessIWant() {
        return n / k;
    }

    @Override
    public String getName() {
        return "XDivK_" + n;
    }
}
