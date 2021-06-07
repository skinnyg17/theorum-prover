package edu.iastate.cs472.proj2;

import java.util.LinkedList;

public class PLResolution {
    LinkedList<Clause> knowledgeBase;
    LinkedList<Clause> goal;
    String goal_sentence;

    public PLResolution(LinkedList<Clause> kb, LinkedList<Clause> g, String s) {
        knowledgeBase = new LinkedList<>();
        goal_sentence = s;
        goal = new LinkedList<>();
        for (int i = 0; i < kb.size(); i++) {
            Clause c = new Clause(kb.get(i).literals, kb.get(i).operator);
            knowledgeBase.add(c);
        }
        for (int i = 0; i < g.size(); i++) {
            Clause c = new Clause(g.get(i).literals, g.get(i).operator);
            goal.add(c);
        }
    }

    private boolean check(Clause c1, Clause c2) {
        for (int i = 0; i < c1.literals.size(); i++) {
            for (int j = 0; j < c2.literals.size(); j++) {
                if(c1.literals.get(i).value.charAt(0) != '~') {
                    if (c2.literals.get(j).value.equals("~" + c1.literals.get(i).value))
                        return true;
                }
                else {
                    if (c2.literals.get(j).value.equals(c1.literals.get(i).value.substring(1)))
                        return true;
                }
            }
        }
        return false;
    }

    public LinkedList<Clause> resolve(Clause c1, Clause c2) {
        LinkedList<Clause> res = new LinkedList<>();
        for (int i = 0; i < c1.literals.size(); i++) {
            for (int j = 0; j < c2.literals.size(); j++) {
                if(c1.literals.get(i).value.charAt(0) != '~') {
                    if (c2.literals.get(j).value.equals("~" + c1.literals.get(i).value)) {
                        c1.literals.remove(i);
                        c2.literals.remove(j);
                    }
                }
                else {
                    if (c2.literals.get(j).value.equals(c1.literals.get(i).value.substring(1))){
                        c1.literals.remove(i);
                        c2.literals.remove(j);
                    }
                }
            }
        }
        if(!c1.literals.isEmpty())
            res.add(c1);
        if(!c2.literals.isEmpty())
            res.add(c2);
        return res;
    }

    //PL Resolution Psuedocode
    public boolean resolution() {
        boolean cont = true;
        LinkedList<Clause> remaining = new LinkedList<>(goal);
        while (true) {
            cont = false;
            for (int i = 0; i < remaining.size(); i++) {
                for (int j = 0; j < knowledgeBase.size(); j++) {
                    if (check(remaining.get(i),knowledgeBase.get(j))) {
                        remaining.get(i).toString();
                        knowledgeBase.get(j).toString();
                        System.out.println("--------------------");
                        LinkedList<Clause> resolvent = resolve(knowledgeBase.remove(j), remaining.remove(i));
                        if (resolvent.size() == 0) {
                            System.out.println("empty clause\n");
                            System.out.println("The KB entails "+goal_sentence+"\n");
                            return true;
                        }

                        for (int k = 0; k < resolvent.size(); k++) {
                            resolvent.get(k).toString();
                        }
                        System.out.println();
                        remaining.addAll(resolvent);
                    }
                }
            }
            if (knowledgeBase.size() == 0 && remaining.size() > 0) {
                System.out.println("No new clauses are added.\n");
                System.out.println("The KB does not entail "+goal_sentence+"\n");
                return false;
            }

            else {
                for (int i = 0; i < remaining.size(); i++) {
                    for (int j = 0; j < knowledgeBase.size(); j++) {
                        if(check(knowledgeBase.get(j),remaining.get(i)))
                            cont = true;
                    }
                }
                if(!cont) {
                    System.out.println("No new clauses are added.\n");
                    System.out.println("The KB does not entail "+goal_sentence+"\n");
                    return false;
                }

            }
        }
    }
}
