import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Merger {

    void update(Path testPath) {
        Pair<String, Integer> hi = getHI(testPath);
        int i = hash2freei.getOrDefault(hi.getKey(), 0);
        hash2freei.put(hi.getKey(), Math.max(i, hi.getValue()+ 1));
    }

    Map<String, Integer> hash2freei = new HashMap<>();

    Pair<String, Integer> getHI(Path test){
        String name = test.getName(test.getNameCount() - 1).toString();
        int l = name.lastIndexOf('_');
        return new Pair<>(name.substring(0, l),  Integer.parseInt(name.substring(l + 1)));
    }

    public void merge(String... dirs) throws IOException {
        Files.walk(Paths.get(dirs[0]))
                .filter(Files::isRegularFile)
                .forEach(this::update);
        Path to = Paths.get(dirs[0]);
        for (int i = 1; i < dirs.length; i++) {
            Files.walk(Paths.get(dirs[i]))
                    .filter(Files::isRegularFile)
                    .forEach(f -> {
                        String hash = getHI(f).getKey();
                        int ind = hash2freei.getOrDefault(hash, 0);
                        hash2freei.put(hash, ind + 1);
                        try {
                            Files.move(f, to.resolve(hash + '_' + ind));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public static void main(String[] args) throws IOException {
        new Merger().merge("regular", "regular1");
    }
}
