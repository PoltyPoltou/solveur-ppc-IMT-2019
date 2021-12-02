package definition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Stack;

/**
 * ConstraintEnum is a constraint linked to the class Operator
 */
public class ConstraintEnum implements Constraint {
    private List<Variable> vars;
    private List<Double> factors;
    private String name;
    private Operator op;

    public ConstraintEnum(Operator o) {
        vars = new ArrayList<>();
        factors = new ArrayList<>();
        op = o;
        name = "NoName";
    }

    public ConstraintEnum(Operator o, String name) {
        vars = new ArrayList<>();
        factors = new ArrayList<>();
        op = o;
        this.name = name;
    }

    public void addVar(Variable v, double d) {
        vars.add(v);
        factors.add(d);
    }

    public void addVar(List<Variable> v, List<Double> d) {
        if (v.size() == d.size() && !d.contains(Double.valueOf(0))) {
            for (int ii = 0; ii < v.size(); ++ii) {
                vars.add(v.get(ii));
                factors.add(d.get(ii));
            }
        } else {
            throw new IllegalArgumentException(
                    "Variables and Factors lists are not the same size when added to Constraint " + this.name);
        }
    }

    private double computeLeftMember() {
        double sum = 0;
        for (int ii = 0; ii < vars.size(); ++ii) {
            sum += vars.get(ii).getValue() * factors.get(ii);
        }
        return sum;
    }

    @Override
    public List<Variable> getVars() {
        return vars;
    }

    @Override
    public boolean isSatisfied() {
        return this.allInstantiated() && this.op.apply(this.computeLeftMember());
    }

    @Override
    public boolean isNecessary() {
        double sum = 0;
        switch (op) {
            case INFERIOR_OR_EQUAL:
                for (int i = 0; i < vars.size(); i++) {
                    if (factors.get(i) > 0) {
                        sum += factors.get(i) * vars.get(i).getInf();
                    } else {
                        sum += factors.get(i) * vars.get(i).getSup();
                    }
                }
                return sum <= 0;
            case SUPERIOR_OR_EQUAL:
                for (int i = 0; i < vars.size(); i++) {
                    if (factors.get(i) > 0) {
                        sum += factors.get(i) * vars.get(i).getSup();
                    } else {
                        sum += factors.get(i) * vars.get(i).getInf();
                    }
                }
                return sum >= 0;
            case NOT_EQUAL:
                return !this.vars.stream().allMatch(v -> (v.getDomainSize() == 1)) || this.isSatisfied();
            default:
                return true;
        }
    }

    @Override
    public boolean allInstantiated() {
        return this.vars.stream().allMatch(Variable::isInstantiated);
    }

    /**
     * @return Variable that was modified during the method filter
     */
    public Variable filter() {
        Predicate<Variable> p = Variable::isInstantiated;
        // Si il reste une seule variable à définir alors on peut filtrer celle-ci
        if (this.vars.stream().filter(p.negate()).count() == 1) {
            boolean isModified = false;
            // il y a un élément dans le stream donc varToFilter != null
            Variable varToFilter = this.vars.stream().filter(p.negate()).findAny().get();
            // On sauvegarde le domaine avant de filtrer pour pouvoir revert au bon moment
            varToFilter.saveDomainBeforeFilter();
            // on calcule le terme de droite de la contrainte sauf la variable a filtrer
            double borne = 0;
            for (int i = 0; i < this.vars.size(); i++) {
                if (vars.get(i) != varToFilter) {
                    borne += factors.get(i) * vars.get(i).getValue();
                }
            }
            // on filtre les valeurs impossibles
            for (int i : varToFilter.getDomain()) {
                if (!this.op.apply(i - borne)) {
                    varToFilter.remValue(i);
                    isModified = true;
                }
            }
            return isModified ? varToFilter : null;
        }
        return null;
    }

}