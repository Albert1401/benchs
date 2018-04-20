package gg;

import tasks.MST;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GG {
    public static List<MST.Edge> generate0RFull(int v){
        List<MST.Edge> gr = new ArrayList<>();
        int number = 0;
        Random random = new Random();
        for (int i = 0; i < v - 1; i++) {
            int zeroTo = random.nextInt(v - i - 1) + i + 1;
            gr.add(new MST.Edge(i, zeroTo, 0, number++));

            for (int j = i + 1; j < v; j++) {
                if (j != zeroTo){
                    gr.add(new MST.Edge(i, j, random.nextDouble() + 0.5, number++));
                }
            }
        }
        return gr;
    }

    public static List<MST.Edge> generate0RDegRandom(int v, int deg){
        List<MST.Edge> gr = new ArrayList<>();
        int number = 0;
        Random random = new Random();
        for (int i = 0; i < v - 1; i++) {
            int zeroTo = random.nextInt(v - i - 1) + i + 1;
            gr.add(new MST.Edge(i, zeroTo, 0, number++));


            int d = 1;
//            while (d != deg){
//                int to =
//            }

            for (int j = i + 1; j < v; j++) {
                if (j != zeroTo){
                    gr.add(new MST.Edge(i, j, random.nextDouble() + 0.5, number++));
                }
            }
        }
        return gr;
    }


}
