package edu.iastate.cs472.proj2;

import java.util.LinkedList;
import java.util.List;

public class Clause {
    LinkedList<Literal> literals;
    String operator;

    public Clause(String op) {
        this.operator = op;
        literals = new LinkedList<Literal>();
    }

    public Clause(LinkedList<Literal> l, String o) {
        literals = new LinkedList<Literal>();
        this.operator = o;
        for (int i = 0; i < l.size(); i++) {
            Literal t = new Literal(l.get(i).value);
            literals.add(t);
        }
    }

    public void addLiterals(LinkedList<Literal> list) {
        for(Literal l: list)
            literals.add(new Literal(l.value));
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < literals.size(); i++) {
            if(i == literals.size()-1)
                res += literals.get(i).value;
            else
                res += literals.get(i).value + " " + operator + " ";
        }
        System.out.println(res);
        return res;
    }
}
