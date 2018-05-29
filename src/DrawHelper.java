import metrics.Benchmark;
import metrics.Benchmarks;
import solvers.Info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrawHelper {

    public static void main(String[] args) {
        String tests = args[0];
        String dir1 = args[1];
        int max = Integer.parseInt(args[2]);
        try (Stream<Path> files = Files.walk(Paths.get(tests))) {
            files.filter(Files::isRegularFile)
                    .collect(Collectors.groupingBy(f -> {
                        //remove $i
                        return f.toString().substring(0, f.toString().lastIndexOf('_'));
                    })).forEach((name, bs) -> {
                List<Info<?>> infos = bs.stream()
                        .map((Function<Path, Info<?>>) f -> Info.fromFile(f.toString()))
                        .collect(Collectors.toList());
                String realName = infos.get(0).getName();
                System.out.println(realName);

                if (infos.stream().anyMatch(i -> !i.getName().equals(realName))) {
                    System.err.println("aaa");
                }
                List<Benchmark.A> res1 = Benchmarks.callsToReachValue(infos, max);

                String sep = File.separator;
                if (name.contains(sep)){
                    name = name.substring(name.lastIndexOf(sep) + sep.length(), name.length());
                }
//                System.out.println(name);
//                System.out.println(sep);
                benchmarkToFile(dir1 + name, realName, res1);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void benchmarkToFile(String path, String trueName, List<Benchmark.A> res) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            writer.write(trueName);
            writer.newLine();
            writer.write(res.size() + "");
            writer.newLine();

            for (Benchmark.A r : res) {
                writer.write(r.f + "");
                writer.newLine();
                writer.write(r.fcallsavg + "");
                writer.newLine();
                writer.write(r.fcallsstd + "");
                writer.newLine();
                r.solversStat.entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .forEach(p -> {
                            try {
                                writer.write(p.getValue() + " ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
