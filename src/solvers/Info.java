package solvers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Info<T> {
    public final int epochs;
    public final List<EpochInfo> epochInfos;
    public final T x;
    protected String name;
    public final int lambda;
    public final double prob;

    public Info(int epochs, List<EpochInfo> epochInfos, T x, int lambda, double prob) {
        this.epochs = epochs;
        this.epochInfos = epochInfos;
        this.x = x;
        this.lambda = lambda;
        this.prob = prob;
    }

    public Info(int epochs, List<EpochInfo> epochInfos, T x) {
        this(epochs, epochInfos, x, -1, -1);
    }


    public String getName() {
        return name;
    }


    public static class EpochInfo {
        public final double fvalue;
        public final int fcalls;
        public int solver;

        public EpochInfo(double fvalue, int fcalls, int solver) {
            this.fvalue = fvalue;
            this.fcalls = fcalls;
            this.solver = solver;
        }

        public EpochInfo(double fvalue, int fcalls) {
            this(fvalue, fcalls, -1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EpochInfo epochInfo = (EpochInfo) o;
            return Double.compare(epochInfo.fvalue, fvalue) == 0 &&
                    fcalls == epochInfo.fcalls;
        }

    }

    public double value() {
        return epochInfos.get(epochInfos.size() - 1).fvalue;
    }

    public int allCalls() {
        return epochInfos.stream().mapToInt(x -> x.fcalls).sum();
    }

    public void toFile(String path, String head) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            writer.write(head);
            writer.newLine();
            writer.write(epochInfos.size() + "");
            writer.newLine();
            for (EpochInfo epochInfo : epochInfos) {
                writer.write(epochInfo.fcalls + "");
                writer.newLine();
                writer.write(epochInfo.fvalue + "");
                writer.newLine();
                writer.write(epochInfo.solver + "");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> Info<T> fromFile(String path) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String name = reader.readLine();
            name = name.substring(0, name.lastIndexOf('$'));
            int epochs = Integer.parseInt(reader.readLine());


            List<EpochInfo> eps = new ArrayList<>(epochs);
            try {
                for (int i = 0; i < epochs; i++) {
                    int fcalls = Integer.parseInt(reader.readLine());
                    double fvalue = Double.parseDouble(reader.readLine());
                    int si = Integer.parseInt(reader.readLine());
                    eps.add(new EpochInfo(fvalue, fcalls, si));
                }
            } catch (Exception e) {
                System.out.println("asd");
            }
            Info<T> info = new Info<>(epochs, eps, null);
            info.name = name;
            return info;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Info info = (Info) o;
        if (epochs != info.epochs || info.epochInfos.size() != this.epochInfos.size()) {
            return false;
        }
        ;
        for (int i = 0; i < epochInfos.size(); i++) {
            if (!info.epochInfos.get(i).equals(this.epochInfos.get(i))) {
                return false;
            }
        }
        return true;
    }

}

