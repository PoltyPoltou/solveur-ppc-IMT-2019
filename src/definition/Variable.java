package definition;

import java.util.Stack;

public class Variable {
    private Domain dom; // le domaine associe
    private Stack<Domain> domainsBeforeFilter;

    public Variable(Domain dom) {
        this.dom = dom;
        this.domainsBeforeFilter = new Stack<>();
        domainsBeforeFilter.add(dom.clone());
    }

    public void reset(Domain dom) {
        this.dom = dom;
    }

    public void saveDomainBeforeFilter() {
        domainsBeforeFilter.add(dom.clone());
    }

    public void revertOneFilter() {
        this.dom = domainsBeforeFilter.pop();
    }

    /**
     * Be careful it returns a copy of the domain so it does not affect loops when
     * instantiate is used next
     * 
     * @return copy of domain
     */
    public Domain getDomain() {
        return this.dom.clone();
    }

    // retourne vrai ssi la variable est instanciee
    public boolean isInstantiated() {
        return this.getDomainSize() == 1;
    }

    // retourne vrai ssi le domaine de la variable contient la valeur v
    public boolean canBeInstantiatedTo(int v) {
        return dom.contains(v);
    }

    // retourne le nombre de valeurs dans le domaine de la variable
    public int getDomainSize() {
        return dom.size();
    }

    // supprime la valeur v du domaine de la variable
    public void remValue(int v) {
        dom.remove(v);
    }

    // supprime toutes les valeurs entre min (inclus) et max (inclus)
    public void remValues(int min, int max) {
        dom.remove(min, max);
    }

    // vide le domaine : supprime toutes ses valeurs
    public void remAllValues() {
        dom.removeAll();
    }

    // instantie la variable a la valeur v
    public void instantiate(int v) {
        this.dom.instantiate(v);
    }

    // retourne la plus petite valeur du domaine
    public int getInf() {
        return dom.firstValue();
    }

    // retourne la plus grande valeur du domaine
    public int getSup() {
        return dom.lastValue();
    }

    // retourne la valeur affectee a la variable ssi la variable est effectivement
    // instanciee, sinon -1
    public int getValue() {
        return this.getDomainSize() == 1 ? this.dom.firstValue() : -1;
    }

    public boolean isEmpty() {
        return this.dom.size() == 0;
    }

}