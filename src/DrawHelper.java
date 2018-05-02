import metrics.Benchmark;
import metrics.Benchmarks;
import solvers.Info;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DrawHelper {

    public static void main(String[] args) {
//        String tests = args[0];
//        String dir1 = args[1];
//        String dir2 = args[2];
        String tests = "111/";
        String dir1 = "AA/";
//        String dir2 = args[2];
        try (Stream<Path> files = Files.walk(Paths.get(tests))) {
            files.filter(Files::isRegularFile)
                    .collect(Collectors.groupingBy(f -> {
                        //remove $i
                        return f.toString().substring(0, f.toString().lastIndexOf('_'));
                    })).forEach((name, bs) -> {
                List<Info> infos = bs.stream()
                        .map(f -> Info.fromFile(f.toString()))
                        .collect(Collectors.toList());

                String realName = infos.get(0).getName();
                if (infos.stream().anyMatch(i -> !i.getName().equals(realName))) {
                    System.err.println("aaa");
                }


                List<Benchmark.A> res1 = Benchmarks.callsToReachValue(infos, 1500, true);
//                        List<Benchmark.A> res2 = Benchmarks.callsToReachValue(infos, 1500, false);
                benchmarkToFile(dir1 + realName, res1);
//                        benchmarkToFile(dir2 + realName, res2);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void benchmarkToFile(String path, List<Benchmark.A> res) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            writer.write(res.size() + "");
            writer.newLine();

            for (Benchmark.A r : res) {
                writer.write(r.f + "");
                writer.newLine();
                writer.write(r.fcalls + "");
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
