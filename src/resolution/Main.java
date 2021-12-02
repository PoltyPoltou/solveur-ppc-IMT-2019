package resolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import definition.Constraint;
import definition.ConstraintEnum;
import definition.Csp;
import definition.Domain;
import definition.DomainBitSet;
import definition.Operator;
import definition.Pair;
import definition.Variable;
import telemetry.CspFactory;
import telemetry.Telemetry;

public class Main {

    public static void main(String[] args) {
        Csp csp1 = new Csp();
        Csp csp2 = new Csp();
        for (int i = 0; i < 3; i++) {
            csp1.getVars().add(new Variable(new DomainBitSet(0, 2)));
            csp2.getVars().add(new Variable(new DomainBitSet(0, 2)));
        }
        ConstraintEnum c1 = new ConstraintEnum(Operator.NOT_EQUAL);
        c1.addVar(csp1.getVars().get(0), 1);
        c1.addVar(csp1.getVars().get(2), -1);
        csp1.getConstraints().add(c1);
        ConstraintEnum c2 = new ConstraintEnum(Operator.SUPERIOR_OR_EQUAL);
        c2.addVar(csp1.getVars().get(0), -1);
        c2.addVar(csp1.getVars().get(1), 1);
        csp1.getConstraints().add(c2);
        ConstraintEnum c3 = new ConstraintEnum(Operator.NOT_EQUAL);
        c3.addVar(csp1.getVars().get(0), -1);
        c3.addVar(csp1.getVars().get(1), 1);
        csp1.getConstraints().add(c3);
        // bruteForceSearch(csp2);
        System.out.println("---------CSP Solving---------");
        // backtrack1(csp1);
        // backtrack3(csp2, new Telemetry(false));
        // testTelemetry(Main::backtrack2, Main::backtrack3);
        testPbExamen(10000000, Main::backtrack3);
    }

    public static void testTelemetry(BiConsumer<Csp, Telemetry> f, BiConsumer<Csp, Telemetry> g) {
        int nbVar = 40;
        int nbConstraints = 45;
        int domainSize = 5;
        List<Pair<Integer, Integer>> domains = new LinkedList<>();
        for (int i = 0; i < nbVar; i++) {
            domains.add(new Pair<>(0, domainSize));
        }
        Csp cspGenerated = CspFactory.generateCsp(nbVar, domains, nbConstraints);
        Telemetry telemetre = new Telemetry(false);
        telemetre.start();
        f.accept(cspGenerated, telemetre);
        telemetre.end(cspGenerated);
        Telemetry tlmtre2 = new Telemetry(false);
        tlmtre2.start();
        g.accept(cspGenerated, tlmtre2);
        tlmtre2.end(cspGenerated);
        assert (tlmtre2.getSolutionCount() == telemetre.getSolutionCount());
    }

    public static void testPbExamen(int n, BiConsumer<Csp, Telemetry> f) {
        Csp cspGenerated = CspFactory.generatePbExamen(n);
        Telemetry telemetre = new Telemetry(false);
        telemetre.start();
        f.accept(cspGenerated, telemetre);
        telemetre.end(cspGenerated);
    }

    // C'est backtrack2 mais avec de quoi mesurer les performances
    public static void backtrack2(Csp csp, Telemetry telemetre) {
        if (csp.allInstanciated()) {
            telemetre.allInstanciated(csp.hasSolution());
        } else {
            Variable y = csp.nextVarToInstanciate();
            Domain storedDomain = y.getDomain();
            for (int val : storedDomain) {
                y.instantiate(val);
                if (csp.hasSolution() && csp.isNecessary()) {
                    backtrack2(csp, telemetre);
                } else {
                    telemetre.backtracked();
                }
            }
            y.reset(storedDomain);
        }
        telemetre.addNodeVisited();
    }
    // ---------------------------------------------------------------------------------------------------
    // bruteForceSearch : on génère toutes les instanciations possibles :
    // aucune contrainte : toute instanciation complète est une solution
    // ---------------------------------------------------------------------------------------------------

    public static void bruteForceSearch(Csp csp) {
        if (csp.allInstanciated()) {
            // traitement du cas où une instanciation est complète
            System.out.println(csp);
        } else {
            Variable y = csp.nextVarToInstanciate();
            Domain storedDomain = y.getDomain();
            for (int val : storedDomain) {
                y.instantiate(val);
                bruteForceSearch(csp);
            }
            y.reset(storedDomain);
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // generateAndTest : on ajoute un test pour vérifier si une instanciation
    // complète est une solution
    // Note : si le csp n'a aucune contrainte, c'est le même comportement que
    // bruteForceSearch
    // ---------------------------------------------------------------------------------------------------

    public static void generateAndTest(Csp csp) {
        if (csp.allInstanciated()) {
            if (csp.hasSolution()) {
                System.out.println(csp);
            }
        } else {
            Variable y = csp.nextVarToInstanciate();
            Domain storedDomain = y.getDomain();
            for (int val : storedDomain) {
                y.instantiate(val);
                generateAndTest(csp);
            }
            y.reset(storedDomain);
        }
    }

    public static void backtrack1(Csp csp) {

        if (csp.allInstanciated()) {
            if (csp.hasSolution()) {
                System.out.println(csp);
            }
        } else {
            Variable y = csp.nextVarToInstanciate();
            Domain storedDomain = y.getDomain();
            for (int val : storedDomain) {
                y.instantiate(val);
                if (csp.hasSolution()) {
                    generateAndTest(csp);
                } else {
                    System.out.println("backtracked !");
                }
            }
            y.reset(storedDomain);
        }
    }

    public static void backtrack3(Csp csp, Telemetry telemetre) {

        if (csp.hasSolution()) {
            if (csp.allInstanciated()) {
                telemetre.allInstanciated(csp.hasSolution());
            } else {
                Variable y = csp.nextVarToInstanciate();
                Domain storedDomain = y.getDomain().clone();
                Stack<Variable> varModified;
                for (int val : storedDomain) {
                    y.instantiate(val);
                    if (csp.isNecessary()) {
                        varModified = propragation(csp, y);
                        while (propragation(csp, y, varModified)) {
                        }
                        backtrack3(csp, telemetre);
                        while (!varModified.empty()) {
                            varModified.pop().revertOneFilter();
                        }
                    } else {
                        telemetre.backtracked();
                    }

                }
                // On remet les variables modifiées par l'instanciation de y
                // Il y a le domaine de y mais aussi la propagation
                y.reset(storedDomain);
            }
        }
        telemetre.addNodeVisited();
    }

    public static Stack<Variable> propragation(Csp csp, Variable varInstanciated) {
        Stack<Variable> varModified = new Stack<>();
        Variable v;
        for (Constraint constraint : csp.getConstraints()) {
            if (constraint.getVars().contains(varInstanciated)) {
                v = constraint.filter();
                if (v != null) {
                    varModified.add(v);
                }
            }
        }
        return varModified;
    }

    /**
     * 
     * @param csp
     * @param varInstanciated
     * @param varModifiedArg  la méthode ajoute en bout de liste les nouvelles
     *                        variables modifiées
     * @return booléen qui indique si on a modifié une variable
     */
    public static boolean propragation(Csp csp, Variable varInstanciated, List<Variable> varModifiedArg) {
        Variable v;
        boolean flag = false;
        for (Constraint constraint : csp.getConstraints()) {
            if (constraint.getVars().contains(varInstanciated)) {
                v = constraint.filter();
                if (v != null) {
                    varModifiedArg.add(v);
                    flag = true;
                }
            }
        }
        return flag;
    }
}
