package solvers.unique;

public class Time {
    private long ss = System.currentTimeMillis();

    public void start(){
        ss = System.currentTimeMillis();
    }

    public void measure(String tag){
        System.out.println(String.format("%s: %d ms", tag, System.currentTimeMillis() - ss));
    }
}
