package tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MST implements Task {
    final double optimVal;
    private final int n;
    private final int v;
    final List<List<Edge>> graph;
    final double[] wEdges;
    private final double w0;
    private final double eps;

    public MST(int n, int v, List<Edge> edges) {
        this.n = n;
        this.v = v;
        visited = new boolean[v];
        graph = new ArrayList<>(n);
        for (int i = 0; i < v; i++) {
            graph.add(new ArrayList<>());
        }
        for (Edge edge : edges) {
            graph.get(edge.from).add(edge);
            graph.get(edge.to).add(new Edge(edge.to, edge.from, edge.w, edge.number));
        }
        wEdges = new double[edges.size()];
        double w0_ = 0;
        for (Edge edge : edges) {
            wEdges[edge.number] = edge.w;
            w0_ += edge.w;
        }
        w0 = w0_;
        int[] colors = new int[v];
        for (int i = 0; i < v; i++) {
            colors[i] = i;
        }
        List<Edge> res = new ArrayList<>();
        edges.sort((e1, e2) -> (int) Math.signum(e1.w - e2.w));

        double opt = 0;
        for (Edge edge : edges) {
            int c1 = colors[edge.from];
            int c2 = colors[edge.to];
            if (c1 != c2) {
                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == c2) {
                        colors[i] = c1;
                    }
                }
                res.add(edge);
                opt += edge.w;
            }
        }
        optimVal = opt;
        eps = edges.stream().mapToDouble(e -> e.w).min().orElse(0) / 2;
        System.out.println(fitnessIWant());
    }

    boolean[] visited;

    private void dfs(int v, boolean[] x) {
        visited[v] = true;
        for (Edge edge : graph.get(v)) {
            if (!visited[edge.to] && x[edge.number]) {
                dfs(edge.to, x);
            }
        }
    }

    @Override
    public double fitness(boolean[] x) {
        int c = 0;
        Arrays.fill(visited, false);

        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) {
                dfs(i, x);
                c++;
            }
        }

        double w = 0;
        for (int i = 0; i < wEdges.length; i++) {
            if (x[i]) {
                w += wEdges[i];
            }
        }

//        return w0 * v - ((c - 1) * w0 + w);
        return (w0 * v - ((c - 1) * w0 + w)) / (w0 * v) * 20;
    }

    @Override
    public int dimension() {
        return n;
    }

    @Override
    public double fitnessIWant() {
        return (w0 * v - optimVal) / (w0 * v) * 20;
    }

    @Override
    public String getName() {
        return "MST_" + n;
    }

    public static class Edge {
        int from;
        int to;
        double w;
        int number;

        public Edge(int from, int to, double w, int number) {
            this.from = from;
            this.to = to;
            this.w = w;
            this.number = number;
        }

        @Override
        public String toString() {
            return "{" + from + "->" + to + "  " + w + "  " + number + "}";
        }
    }
}