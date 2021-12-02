package telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import definition.Constraint;
import definition.ConstraintEnum;
import definition.Csp;
import definition.Domain;
import definition.DomainBitSet;
import definition.Operator;
import definition.Pair;
import definition.Variable;

public abstract class CspFactory {
    public static Csp generateCsp(int numberVariable, List<Pair<Integer, Integer>> domain, int nbConstraint) {
        // check Consistency of parameters
        if (domain.size() != numberVariable) {
            throw new IllegalArgumentException("The number of domain and the number of Variables don't match "
                    + numberVariable + " != " + domain.size());
        }
        Random rd = new Random();
        Csp csp = new Csp();
        // init variables and their domains
        List<Variable> vars = csp.getVars();
        for (int i = 0; i < numberVariable; i++) {
            vars.add(new Variable(new DomainBitSet(domain.get(i).getFirst(), domain.get(i).getSecond())));
        }
        // We generate the contraints
        List<Constraint> constraints = csp.getConstraints();
        Operator[] values = Operator.values();
        ConstraintEnum cons;
        List<Integer> randomListOfIntegers = new ArrayList<>();
        for (int i = 0; i < vars.size(); i++) {
            randomListOfIntegers.add(i);
        }
        for (int i = 0; i < nbConstraint; i++) {
            cons = new ConstraintEnum(values[rd.nextInt(values.length)], "Random generated Constraint n°" + i);
            Collections.shuffle(randomListOfIntegers);
            for (int j = 0; j < 2; j++) {
                cons.addVar(vars.get(randomListOfIntegers.get((j + i) % vars.size())), rd.nextDouble() * 10 - 5);
            }
            constraints.add(cons);
        }
        return csp;
    }

    public static Csp generatePbExamen(int n) {
        Csp csp = new Csp();
        List<Variable> vars = csp.getVars();
        List<Constraint> constraints = csp.getConstraints();
        Domain dom = new DomainBitSet(1, 10);
        vars.add(new Variable(dom.clone()));
        ConstraintEnum c;
        for (int i = 1; i < n; i++) {
            c = new ConstraintEnum(Operator.INFERIOR_OR_EQUAL);
            vars.add(new Variable(dom.clone()));
            c.addVar(vars.get(i - 1), 1);
            c.addVar(vars.get(i), -1);
            constraints.add(c);
            c = new ConstraintEnum(Operator.NOT_EQUAL);
            c.addVar(vars.get(i - 1), 1);
            c.addVar(vars.get(i), -1);
            constraints.add(c);
        }
        c = new ConstraintEnum(Operator.INFERIOR_OR_EQUAL);
        vars.add(new Variable(dom.clone()));
        c.addVar(vars.get(n - 1), 1);
        c.addVar(vars.get(0), -1);
        constraints.add(c);
        c = new ConstraintEnum(Operator.NOT_EQUAL);
        vars.add(new Variable(dom.clone()));
        c.addVar(vars.get(n - 1), 1);
        c.addVar(vars.get(0), -1);
        constraints.add(c);
        return csp;
    }
}
