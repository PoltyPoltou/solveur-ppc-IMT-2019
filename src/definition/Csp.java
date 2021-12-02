package definition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Csp {

    List<Variable> vars; // l'ensemble des variables du CSP. Note: les domaines sont connus au travers
                         // des variables
    List<Constraint> cons; // l'ensemble des contraintes du CSP

    public Csp() {
        this.vars = new ArrayList<>();
        this.cons = new ArrayList<>();
    }

    public List<Variable> getVars() {
        return this.vars;
    }

    public List<Constraint> getConstraints() {
        return this.cons;
    }

    /**
     * retourne la premiere variable non instanciee du csp
     * 
     * @return null if all vars are instanciated
     */
    public Variable nextVarToInstanciate() {
        for (Variable variable : vars) {
            if (!variable.isInstantiated()) {
                return variable;
            }
        }
        return null;
    }

    // retourne vrai ssi toutes les variables sont instanciées
    public boolean allInstanciated() {
        return nextVarToInstanciate() == null;
    }

    // retourne vrai ssi le CSP possède (au moins) une solution :
    // l'ensemble des contraintes du CSP est vérifié
    // ATTENTION : ce n'est pas la seule condition
    public boolean hasSolution() {
        for (Constraint c : cons) {
            if (c.allInstantiated() && !c.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    public boolean isNecessary() {
        return this.cons.stream().allMatch(Constraint::isNecessary);
    }

    public String toString() {
        String str = "Solution : \n";
        for (Variable v : this.vars) {
            str += "var " + this.vars.indexOf(v) + " = " + v.getValue() + " \n";
        }
        return str;
    }
}
