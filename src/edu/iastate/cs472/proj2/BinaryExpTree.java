package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class BinaryExpTree {
    ArrayList<Node> trees;
    ArrayList<ArrayList<String>> args;
    Stack<String> opStack = new Stack<String>();
    Stack<Node> nodeStack = new Stack<Node>();

    public BinaryExpTree(ArrayList<ArrayList<String>> s) {
        this.args = s;
        trees = new ArrayList<Node>();
    }

    public ArrayList<Node> getTrees() {
        for (ArrayList<String> list : args) {
            for (int i=0; i< list.size(); i++) {
                String c = list.get(i);
                if(!isOperator(c)) {
                    Node temp = new Node(c);
                    nodeStack.push(temp);
                }
                else if(getPriority(c) == -1)
                    opStack.push(c);
                else if(getPriority(c) == 0) {
                    while (!opStack.empty()) {
                        String t = opStack.pop();
                        if (getPriority(t) != -1) {
                            nodeStack.push(addOp(t));
                        }
                        else
                            continue;
                    }
                }
                else if (isOperator(c)) {
                    if(opStack.isEmpty())
                        opStack.push(c);
                    else {
                        while (!opStack.empty()) {
                            String t = opStack.pop();
                            if (getPriority(t) == -1) {
                                opStack.push(t);
                                break;
                            }
                            else if(getPriority(c) > 0) {
                                if(getPriority(t) < getPriority(c)) {
                                    opStack.push(t);
                                    break;
                                }
                                else {
                                    nodeStack.push(addOp(t));
                                }
                            }
                        }
                        opStack.push(c);
                    }
                }
            }
            if(opStack.empty()) {
                trees.add(nodeStack.pop());
            }
            else {
                while (!opStack.empty()) {
                    String t = opStack.pop();
                    if(getPriority(t) > 0) {
                        nodeStack.push(addOp(t));
                    }
                }
                trees.add(nodeStack.pop());
            }
            nodeStack = new Stack<Node>();
            opStack = new Stack<String>();
        }
        return trees;
    }


    public Node convertCnf(Node n) {
        if(n.left == null && n.right == null)
            return leafCnf(n);
        else if(n.value.equals("&&"))
            return andCnf(n);
        else if(n.value.equals("||"))
            return orCnf(n);
        else if(n.unary)
            return negateCnf(n);
        else if(n.value.equals("=>"))
            return implCnf(n);
//        else
        return doubleImplCnf(n);
    }


    private Node doubleImplCnf(Node n) {
        convertCnf(n.left);
        convertCnf(n.right);
        Node l1 = new Node(n.left.value,n.left.parent,n.left.left,n.left.right,n.left.unary,n.left.cnf);
        Node l2 = new Node(n.right.value,n.right.parent,n.right.left,n.right.right,n.right.unary,n.right.cnf);
        Node r1 = new Node(n.right.value,n.right.parent,n.right.left,n.right.right,n.right.unary,n.right.cnf);
        Node r2 = new Node(n.left.value,n.left.parent,n.left.left,n.left.right,n.left.unary,n.left.cnf);
        Node t1 = new Node("=>");
        t1.left = l1;
        t1.right = l2;
        n.left = implCnf(t1);
        Node t2 = new Node("=>");
        t2.left = r1;
        t2.right = r2;
        n.right = implCnf(t2);
        return andCnf(n);
    }

    private Node implCnf(Node n) {
        Node temp = new Node("~", true);
//        temp.parent = n;
//        n.left.parent = temp;
        temp.left = n.left;
        n.left = temp;
        return orCnf(n);
    }

    private Node negateCnf(Node n) {
        Node t;
        if(n.left.cnf.clauses.isEmpty()) {
            t = convertCnf(n.left);
        }
        else
            t = n.left;
        if(t.left == null && t.right == null) {
            String temp = "";
            if(t.cnf.clauses.get(0).literals.get(0).value.charAt(0) == '~') {
                temp += t.cnf.clauses.get(0).literals.get(0).value.substring(1);
            }
            else {
                temp = "~";
                temp += t.cnf.clauses.get(0).literals.get(0).value;
            }
            t.cnf.clauses.get(0).literals.get(0).value = temp;
            n.cnf.clauses.add(t.cnf.clauses.get(0));
        }
        else if(t.value.equals("&&")) {
            Clause c = new Clause("||");
            for (int i = 0; i < t.cnf.clauses.size(); i++) {
                c.addLiterals(t.cnf.clauses.get(i).literals);
            }
            for (int i = 0; i < c.literals.size(); i++) {
                if(c.literals.get(i).value.charAt(0) == '~') {
                    String temp = c.literals.get(i).value.substring(1);
                    c.literals.get(i).value = temp;
                }
                else {
                    String temp = "~";
                    temp += c.literals.get(i).value;
                    c.literals.get(i).value = temp;
                }
            }
            n.cnf.clauses.add(c);
        }
        else if(t.value.equals("||")) {
            //for each clause first flip the sign and the literals
            for (int i = 0; i < t.cnf.clauses.size(); i++) {
                t.cnf.clauses.get(i).operator = "&&";
                for (int j = 0; j < t.cnf.clauses.get(i).literals.size(); j++) {
                    if(t.cnf.clauses.get(i).literals.get(j).value.charAt(0) == '~')
                        t.cnf.clauses.get(i).literals.get(j).value = t.cnf.clauses.get(i).literals.get(j).value.substring(1);
                    else
                        t.cnf.clauses.get(i).literals.get(j).value = "~"+t.cnf.clauses.get(i).literals.get(j).value;
                }
            }
            LinkedList<Clause> list = new LinkedList<>();
            ArrayList<Literal> tautLits = new ArrayList<>();
            for (int i = 0; i < t.cnf.clauses.size(); i++) {
                int s1 = t.cnf.clauses.get(i).literals.size();
                for (int j = 0; j < s1; j++) {
                    for (int k = i+1; k < t.cnf.clauses.size(); k++) {
                        int s2 = t.cnf.clauses.get(k).literals.size();
                        for (int l = 0; l < s2; l++) {
                            Clause c = new Clause("||");

                            c.literals.add(new Literal(t.cnf.clauses.get(i).literals.get(j).value));
                            if(!checkRepeat(c,t.cnf.clauses.get(k).literals.get(l)))
                                c.literals.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                            else {
                                tautLits.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                                list.add(c);
                            }
                            if(checkTautLits(c, tautLits)) {
                                if(!tautology(c))
                                    list.add(c);
                            }
                        }
                    }
                }
            }
            if(!list.isEmpty())
                n.cnf.clauses = removeClausesWithTautLits(list, tautLits);
            else
                n.cnf.clauses = t.cnf.clauses;
//            for (int i = 0; i < t.cnf.clauses.size(); i++) {
//                for (int j = 0; j < t.cnf.clauses.get(i).literals.size(); j++) {
//                    Clause c = new Clause("&&");
//                    if(t.cnf.clauses.get(i).literals.get(j).value.charAt(0) == '~') {
//                        String temp = t.cnf.clauses.get(i).literals.get(j).value.substring(1);
//                        c.literals.add(new Literal(temp));
//                    }
//                    else {
//                        String temp = "~" + t.cnf.clauses.get(i).literals.get(j).value;
//                        c.literals.add(new Literal(temp));
//                    }
//                    list.add(c);
//                }
//            }
//            n.cnf.clauses = list;
        }
        else if(t.value.equals("=>")) {
            for (int i = 0; i < t.cnf.clauses.size(); i++) {
                t.cnf.clauses.get(i).operator = "&&";
                for (int j = 0; j < t.cnf.clauses.get(i).literals.size(); j++) {
                    if(t.cnf.clauses.get(i).literals.get(j).value.charAt(0) == '~')
                        t.cnf.clauses.get(i).literals.get(j).value = t.cnf.clauses.get(i).literals.get(j).value.substring(1);
                    else
                        t.cnf.clauses.get(i).literals.get(j).value = "~"+t.cnf.clauses.get(i).literals.get(j).value;
                }
            }
            LinkedList<Clause> list = new LinkedList<>();
            for (int i = 0; i < t.cnf.clauses.size(); i++) {
                int s1 = t.cnf.clauses.get(i).literals.size();
                for (int j = 0; j < s1; j++) {
                    for (int k = i+1; k < t.cnf.clauses.size(); k++) {
                        int s2 = t.cnf.clauses.get(k).literals.size();
                        for (int l = 0; l < s2; l++) {
                            Clause c = new Clause("||");
                            ArrayList<Literal> tautLits = new ArrayList<>();
                            c.literals.add(new Literal(t.cnf.clauses.get(i).literals.get(j).value));
                            if(!checkRepeat(c,t.cnf.clauses.get(k).literals.get(l)))
                                c.literals.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                            else
                                tautLits.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                            if(checkTautLits(c, tautLits)) {
                                if(!tautology(c))
                                    list.add(c);
                            }
                        }
                    }
                }
            }
            n.cnf.clauses = list;
        }
        else if(t.value.equals("<=>")) {
            if(t.cnf.clauses.size() > 1) {
                for (int i = 0; i < t.cnf.clauses.size(); i++) {
                    t.cnf.clauses.get(i).operator = "&&";
                    for (int j = 0; j < t.cnf.clauses.get(i).literals.size(); j++) {
                        if(t.cnf.clauses.get(i).literals.get(j).value.charAt(0) == '~')
                            t.cnf.clauses.get(i).literals.get(j).value = t.cnf.clauses.get(i).literals.get(j).value.substring(1);
                        else
                            t.cnf.clauses.get(i).literals.get(j).value = "~"+t.cnf.clauses.get(i).literals.get(j).value;
                    }
                }
                LinkedList<Clause> list = new LinkedList<>();
                for (int i = 0; i < t.cnf.clauses.size(); i++) {
                    int s1 = t.cnf.clauses.get(i).literals.size();
                    for (int j = 0; j < s1; j++) {
                        for (int k = i+1; k < t.cnf.clauses.size(); k++) {
                            int s2 = t.cnf.clauses.get(k).literals.size();
                            for (int l = 0; l < s2; l++) {
                                Clause c = new Clause("||");
                                ArrayList<Literal> tautLits = new ArrayList<>();
                                c.literals.add(new Literal(t.cnf.clauses.get(i).literals.get(j).value));
                                if(!checkRepeat(c,t.cnf.clauses.get(k).literals.get(l)))
                                    c.literals.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                                else
                                    tautLits.add(new Literal(t.cnf.clauses.get(k).literals.get(l).value));
                                if(checkTautLits(c, tautLits)) {
                                    if(!tautology(c))
                                        list.add(c);
                                }
                            }
                        }
                    }
                }
                n.cnf.clauses = list;
            }
        }
        return n;
    }

    private Node leafCnf(Node n) {
        if(!n.cnf.clauses.isEmpty())
            return n;
        Literal l = new Literal(n.value);
        Clause c = new Clause((n.parent == null) ? "" : n.parent.value);
        c.literals.add(l);
        n.cnf.clauses.add(c);
        return n;
    }

    private Node andCnf(Node n) {
        Node t1;
        Node t2;
        if(n.left.cnf.clauses.isEmpty()) {
            t1 = convertCnf(n.left);
        }
        else
            t1 = n.left;
        if(n.right.cnf.clauses.isEmpty()) {
            t2 = convertCnf(n.right);
        }
        else
            t2 = n.right;
        for (int i = 0; i < t1.cnf.clauses.size(); i++) {
            n.cnf.clauses.add(t1.cnf.clauses.get(i));
        }
        for (int i = 0; i < t2.cnf.clauses.size(); i++) {
            n.cnf.clauses.add(t2.cnf.clauses.get(i));
        }
        return n;
    }

    private Node orCnf(Node n) {
        Node t1;
        Node t2;
        if(n.left.cnf.clauses.isEmpty()) {
            t1 = convertCnf(n.left);
        }
        else
            t1 = n.left;
        if(n.right.cnf.clauses.isEmpty()) {
            t2 = convertCnf(n.right);
        }
        else
            t2 = n.right;
        ArrayList<Literal> repeat = new ArrayList<>();
        if(t1.cnf.clauses.size() == 0)
            n.cnf.addClauses(t2.cnf.clauses);
        else if(t2.cnf.clauses.size() == 0)
            n.cnf.addClauses(t1.cnf.clauses);
        for (int i = 0; i < t1.cnf.clauses.size(); i++) {
            for (int j = 0; j < t2.cnf.clauses.size(); j++) {
                Clause c = new Clause("||");
                c.addLiterals(t1.cnf.clauses.get(i).literals);
                for (int k = 0; k < t2.cnf.clauses.get(j).literals.size(); k++) {
                    if(checkRepeat(c,t2.cnf.clauses.get(j).literals.get(k)))
                        repeat.add(t2.cnf.clauses.get(j).literals.get(k));
                    else
                        c.literals.add(new Literal(t2.cnf.clauses.get(j).literals.get(k).value));
                }
                if(c.literals.size() > 1 &&checkTautLits(c, repeat)) {
                    if(!tautology(c))
                        n.cnf.clauses.add(c);
                }
                else if(c.literals.size() == 1)
                    n.cnf.clauses.add(c);
            }
        }
        return n;
    }

    private Node addOp(String op) {
        Node res;
        if(getPriority(op) == 5) {
            res = new Node(op, true);
            res.left = nodeStack.pop();
            res.left.parent = res;
        }
        else {
            res = new Node(op);
            res.right = nodeStack.pop();
            res.left = nodeStack.pop();
            res.left.parent = res;
            res.right.parent = res;
        }
        return res;
    }

    private boolean checkRepeat(Clause c, Literal l) {
        for (int i = 0; i < c.literals.size(); i++) {
            if(c.literals.get(i).value.equals(l.value))
                return true;
        }
        return false;
    }

    private boolean checkTautLits(Clause c, ArrayList<Literal> lits) {
        for (int i = 0; i < lits.size(); i++) {
            for (int j = 0; j < c.literals.size(); j++) {
                if(c.literals.get(j).value.equals(lits.get(i).value))
                    return false;
            }
        }
        return true;
    }

    private boolean tautology(Clause c) {
        for (int i = 0; i < c.literals.size(); i++) {
            for (int j = i+1; j < c.literals.size(); j++) {
                if(c.literals.get(i).value.charAt(0) != '~') {
                    if (c.literals.get(j).value.equals("~" + c.literals.get(i).value))
                        return true;
                }
                else {
                    if (c.literals.get(j).value.equals(c.literals.get(i).value.substring(1)))
                        return true;
                }
            }
        }
        return false;
    }

    public void removeDuplicateClauses(Node n) {
        /*
            first check for same operator
            A || B
            ^
            A || B
            if same operator, then check literals
                if int repeat_lits == clause.literals.size, remove the clause
         */
        LinkedList<Clause> c = n.cnf.clauses;
        for (int i = 0; i < c.size(); i++) {
            String opr = c.get(i).operator;
            LinkedList<Literal> lits = c.get(i).literals;
            for (int j = 0; j < c.size(); j++) {
                if(j==i)
                    continue;
                if(c.get(j).operator.equals(opr) && c.get(j).literals.size() == lits.size()) {
                    boolean isSame = false;
                    for (int k = 0; k < c.get(j).literals.size(); k++) {
                        if(c.get(j).literals.get(k).value.equals(lits.get(k).value))
                            isSame = true;
                        else {
                            isSame = false;
                            break;
                        }
                    }
                    if (isSame)
                        c.remove(j);
                }
            }
        }
        n.cnf.clauses = c;
    }

    private LinkedList<Clause> removeClausesWithTautLits(LinkedList<Clause> c, ArrayList<Literal> taut) {
        for (int i = 0; i < taut.size(); i++) {
            for (int j = 0; j < c.size(); j++) {
                for (int k = 0; k < c.get(j).literals.size(); k++) {
                    if(c.get(j).literals.get(k).value.equals(taut.get(i).value)) {
                        if(c.get(j).literals.size()>1) {
                            c.remove(j);
                            j--;
                            break;
                        }
                    }
                }
            }
        }
        return c;
    }

    private boolean isOperator(String s) {
        if(s.matches("~") || s.matches("&&") || s.matches("=>") || s.equals("<=>") || s.matches("\\(") || s.matches("\\)") ||
                (s.charAt(0)=='|' && s.charAt(1)=='|'))
            return true;
        return false;
    }

    private int getPriority(String s) {
        int res = -2;
        switch (s) {
            case "~":
                res = 5;
                break;
            case "&&":
                res = 4;
                break;
            case "||":
                res = 3;
                break;
            case "=>":
                res = 2;
                break;
            case "<=>":
                res = 1;
                break;
            case "(":
                res = -1;
                break;
            default:
                res = 0;
        }
        return res;
    }
}
