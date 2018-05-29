package tasks;

import javafx.util.Pair;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.Tour;
import solvers.EASolver;
import solvers.Info;
import solvers.OneLambda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class TSP implements Task<int[], Pair<int[], int[]>> {
    final TSPInstance instance;
    final double bestDistance;
    final String path;

    public TSP(String path, String bestTourPath){
        this.path = path;
        try {
            this.instance = new TSPInstance(new File(path));
            this.bestDistance = new TSPInstance(new File(bestTourPath)).getTours().get(0).distance(instance);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] init() {
        return Tour.createRandomTour(instance.getDimension()).toArray();
    }

    @Override
    public List<Pair<int[], int[]>> initInds(int lambda) {
        List<Pair<int[], int[]>> gen = new ArrayList<>(lambda);
        for (int i = 0; i < lambda; i++) {
            gen.add(new Pair<>(new int[dimension() + 1], new int[dimension() + 1]));
        }
        return gen;
    }

    @Override
    public void generate(double p, List<Pair<int[], int[]>> tos) {
        Random random = new Random();
        for (int i = 0; i < tos.size(); i++) {
            PoissonDistribution.getInds(dimension(), p, tos.get(i).getKey(), true);
            int[] r = tos.get(i).getValue();
            int n = tos.get(i).getKey()[0];
            for (int j = 1; j <= n; j++) {
                r[j] = random.nextInt(dimension());
            }
        }
    }

    void permute(int x[], int i, int j){
        int t = i;
        i = Math.min(i, j);
        j = Math.max(t, j);
        while (i < j){
            t = x[i];
            x[i] = x[j];
            x[j] = t;
            i++;
            j--;
        }
    }

    @Override
    public void mutate(int[] x, Pair<int[], int[]> inds) {
        int[] is = inds.getKey();
        int[] js = inds.getValue();
        for (int i = 1; i <= is[0]; i++) {
            permute(x, is[i], js[i]);
        }
    }

    @Override
    public void rev(int[] x, Pair<int[], int[]> inds) {
        int[] is = inds.getKey();
        int[] js = inds.getValue();
        for (int i = is[0]; i >= 1; i--) {
            permute(x, is[i], js[i]);
        }
    }

    @Override
    public double fitness(int[] x) {
        return bestDistance / Tour.distance(instance, x);
    }

    @Override
    public double fitness(int[] x, Pair<int[], int[]> inds, double f) {
        mutate(x, inds);
        double res = fitness(x);
        rev(x, inds);
        return res;
    }

    @Override
    public int dimension() {
        return instance.getDimension();
    }

    @Override
    public double fitnessIWant() {
        return 1.0;
    }

    @Override
    public String getName() {
        return "TCP_" + path.substring(path.lastIndexOf('/') + 1, path.length());
    }

//    public static void main(String[] args) throws IOException {
//        TSPInstance tsp = new TSPInstance(new File("TSPLIB4J/data/tsp/berlin52.tsp"));
//        TSPInstance tsp1 = new TSPInstance(new File("TSPLIB4J/data/tsp/berlin52.opt.tour"));
//        Tour tour = tsp1.getTours().get(0);
//        double res = 0;
//        for (int i = 0; i < tour.toArray().length; i++){
//            if (i != 0) {
//                res += tsp.getDistanceTable().getDistanceBetween(tour.get(i), tour.get(i - 1));
//            }
//            System.out.println(tour.toArray()[i]);
//        }
//        res += tsp.getDistanceTable().getDistanceBetween(22, 1);
//
//        System.out.println(res + "\t" + tour.distance(tsp));
//        System.out.println(tour.size());
//        Tour r = Tour.createRandomTour(tsp.getDimension());
//
//
//        TSP tsp_ = new TSP("TSPLIB4J/data/tsp/berlin52.tsp", "TSPLIB4J/data/tsp/berlin52.opt.tour");
//        System.out.println("----------------");
//        System.out.println(tsp_.fitnessIWant() + "");
//        System.out.println(tsp_.dimension() + "\t" + tsp.getDimension());
//        System.out.println(1.0 / r.distance(tsp) + "\t" + tsp_.fitness(r.toArray()));
//        System.out.println(r.toArray().length + "");
//        List<Pair<int[], int[]>> pairs = tsp_.initInds(500);
//        tsp_.generate(0.1, pairs);
//
//        boolean b = pairs.stream().allMatch(p -> {
//            int[] x = r.toArray();
//            int[] x1 = x.clone();
//            tsp_.mutate(x, p);
//            return tsp_.fitness(x) == 1.0 / Tour.createTour(x).distance(tsp);
//            tsp_.rev(x, p);
//            return Arrays.equals(x, x1);
//        });
//        System.out.println(b);
//    }

    public static void main(String[] args) {
        TSP tsp = new TSP("TSPLIB4J/data/tsp/bayg29.tsp", "TSPLIB4J/data/tsp/bayg29.opt.tour");
        EASolver solver = new OneLambda(3, 50_000_000);
        Info<int[]> info = solver.solve(tsp);
        System.out.println(Arrays.toString(info.x));
        System.out.println(info.allCalls() + "\t" + info.value());
    }
}