package edu.iastate.cs472.proj2;

public class Node {
    public String value;
    public Node parent;
    public Node left;
    public Node right;
    public boolean unary;
    public ConjuctiveNormalForm cnf;

    public Node(String s) {
        value = s;
        left = right = parent = null;
        unary = false;
        cnf = new ConjuctiveNormalForm();
    }

    public Node(String c, Node p, Node l, Node r) {
        this.value = c;
        this.parent = p;
        this.left = l;
        this.right = r;
        this.cnf = new ConjuctiveNormalForm();
        this.unary = false;
    }

    public Node(String val,Node p, Node l, Node r, boolean t, ConjuctiveNormalForm c) {
        this.value = val;
        this.parent = p;
        this.left = l;
        this.right = r;
        this.cnf = new ConjuctiveNormalForm(c.clauses);
        this.unary = t;
    }

    public Node(String s, boolean u) {
        unary = true;
        value = s;
        left = right = parent = null;
        cnf = new ConjuctiveNormalForm();
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < cnf.clauses.size(); i++) {
            if(i == cnf.clauses.size()-1)
                res += "( " + cnf.clauses.get(i).toString() + " )";
            else
                res += "( "+ cnf.clauses.get(i).toString() + " ) && ";
        }
        return res;
    }
}
