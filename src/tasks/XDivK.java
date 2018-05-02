package tasks;

import java.util.List;

public class XDivK implements Task{
    private int n, k;

    public XDivK(int n, int k) {
        this.n = n;
        this.k = k;
    }

    @Override
    public double fitness(boolean[] x) {
        int res = 0;
        for (boolean b : x) {
            if (b){
                res+=1;
            }
        }
        return res / k;
    }

    @Override
    public double fitness(boolean[] x, int[] inds, double f) {
        throw new RuntimeException("not implemented");
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
