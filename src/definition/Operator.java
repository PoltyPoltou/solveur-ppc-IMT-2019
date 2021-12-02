package definition;

import java.util.function.Predicate;

/**
 * these are the basics operators to pass as arguments for the constraints they
 * are all relative to 0 (right member)
 */

public enum Operator {

    INFERIOR_OR_EQUAL((l) -> l <= 0), SUPERIOR_OR_EQUAL((l) -> l <= 0), NOT_EQUAL((l) -> l != 0);

    // For EQUAL Operator we have a static error
    static final double ERROR = 0.001;

    private final Predicate<Double> comparaison;

    private Operator(final Predicate<Double> c) {
        this.comparaison = c;
    }

    public boolean apply(double d) {
        return this.comparaison.test(d);
    }
}
