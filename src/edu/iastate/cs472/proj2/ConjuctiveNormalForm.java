package edu.iastate.cs472.proj2;

import java.util.LinkedList;

public class ConjuctiveNormalForm {
    LinkedList<Clause> clauses;

    public ConjuctiveNormalForm() {
        clauses = new LinkedList<Clause>();
    }

    public ConjuctiveNormalForm(LinkedList<Clause> cl) {
        clauses = new LinkedList<Clause>();
        for (int i = 0; i < cl.size(); i++) {
            Clause c = new Clause(cl.get(i).literals, cl.get(i).operator);
            clauses.add(c);
        }
    }

    public void addClauses(LinkedList<Clause> list) {
        for (Clause c : list)
            clauses.add(c);
    }

}
