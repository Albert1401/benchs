package solvers;

import java.util.Objects;

public class State {
    int fstate, pstate, lstate;
    Type type;

    public State(double f, int fstates, double fmax,
                 double p, int lambda, int LP, int PP,
                 int dim,
                 Type type) {
        this.type = type;
//        type = Type.ALL;
        fstate = (int) (f / (1.01 * fmax) * fstates);
//        lstate = (int) Math.log(lambda);
        lstate = lambda / LP;
        pstate = (int) (p * dim) / PP;

        switch (type) {
            case WITHOUTLAMBDA:
                lstate = -1;
                break;
            case WITHOUTMUT:
                pstate = -1;
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return fstate == state.fstate &&
                pstate == state.pstate &&
                lstate == state.lstate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fstate, pstate, lstate);
    }

    public enum Type {
        ALL,
        WITHOUTMUT,
        WITHOUTLAMBDA
    }
}
