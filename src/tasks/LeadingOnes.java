package tasks;

import solvers.AbstractEASolver;

public class LeadingOnes extends BTask {
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
    public double fitness(boolean[] x, int[] inds, double f) {
        int size = inds[0];
        for (int i = 1; i <= size; i++) {
            int ind = inds[i];
            if (ind < f) {
                return ind;
            } else {
                break;
            }
        }
        mutate(x, inds);
        int f1 = 0;
        for (int i = (int) f; i < x.length; i++) {
            if (x[i]) {
                f1++;
            } else {
                break;
            }
        }
        rev(x, inds);
        return f + f1;
    }

    public static void main(String[] args) {
        LeadingOnes ones = new LeadingOnes(10);
//        boolean x[] = new boolean[] {true, true, true, true, false, false, true, false, true, true};
        boolean x[] = new boolean[] {true, true, true, true, true, true, true, true, true, true};
//        List<Integer> ind = Arrays.asList(4, 5, 7);
        int[] ind = new int[]{9};
        double f = ones.fitness(x, ind, 10.0);
        System.out.println(f);
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
