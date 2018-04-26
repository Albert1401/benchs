package solvers;

import tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class Info {
    public final int epochs;
    public final List<EpochInfo> epochInfos;
    public final boolean[] x;
    protected String name;
    public final int lambda;
    public final double prob;

    public Info(int epochs, List<EpochInfo> epochInfos, boolean[] x, int lambda, double prob) {
        this.epochs = epochs;
        this.epochInfos = epochInfos;
        this.x = x;
        this.lambda = lambda;
        this.prob = prob;
    }

    public Info(int epochs, List<EpochInfo> epochInfos, boolean[] x) {
        this(epochs, epochInfos, x, -1, -1);
    }


    public String getName() {
        return name;
    }


    public static class EpochInfo {
        public final double fvalue;
        public final int fcalls;
        public final boolean success;
        public int solver;

        public EpochInfo(double fvalue, int fcalls, boolean success, int solver) {
            this.fvalue = fvalue;
            this.fcalls = fcalls;
            this.success = success;
            this.solver = solver;
        }

        public EpochInfo(double fvalue, int fcalls, boolean success) {
            this(fvalue, fcalls, success, -1);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EpochInfo epochInfo = (EpochInfo) o;
            return Double.compare(epochInfo.fvalue, fvalue) == 0 &&
                    fcalls == epochInfo.fcalls &&
                    success == epochInfo.success;
        }

    }

    public double value() {
        return epochInfos.get(epochs - 1).fvalue;
    }

    public int allCalls() {
        return epochInfos.stream().mapToInt(x -> x.fcalls).sum();
    }

    public void toFile(String path, String head){
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))){
            writer.write(head);
            writer.newLine();
            writer.write(epochs + "");
            writer.newLine();
            for (EpochInfo epochInfo : epochInfos) {
                writer.write(epochInfo.fcalls + "");
                writer.newLine();
                writer.write(epochInfo.fvalue + "");
                writer.newLine();
                writer.write(epochInfo.solver + "");
                writer.newLine();
                writer.write((epochInfo.success ? 1 : 0) + "");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Info fromFile(String path){
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))){
            String name = reader.readLine();
            //TODO
            name = name.substring(0, name.length() - 2);
            int epochs = Integer.parseInt(reader.readLine());


            List<EpochInfo> eps = new ArrayList<>(epochs);
            for (int i = 0; i < epochs; i++) {
                int fcalls = Integer.parseInt(reader.readLine());
                double fvalue = Double.parseDouble(reader.readLine());
                int si = Integer.parseInt(reader.readLine());
                int success = Integer.parseInt(reader.readLine());
                eps.add(new EpochInfo(fvalue, fcalls, success == 1, si));
            }
            Info info = new Info(epochs, eps, null);
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
        if (epochs != info.epochs || info.epochInfos.size() != this.epochInfos.size()){
            return false;
        };
        for (int i = 0; i < epochInfos.size(); i++) {
            if (!info.epochInfos.get(i).equals(this.epochInfos.get(i))){
                return false;
            }
        }
        return true;
    }

}

