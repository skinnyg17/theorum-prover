package edu.iastate.cs472.proj2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
//        System.out.println("Enter file name: ");
//        Scanner s = new Scanner(System.in);
//        String fileName = s.nextLine();
        File f = new File("kb.txt");

        Reader r = new Reader(f);
        ArrayList<ArrayList<String>> kb = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> p = new ArrayList<ArrayList<String>>();
        ArrayList<String> goal_sentences = new ArrayList<>();
        try {
             kb = r.getKbSenteces();
             p = r.getProofs();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < p.size(); i++) {
            String s = "";
            for (int j = 0; j < p.get(i).size(); j++) {
                if(p.get(i).get(j).equals("~"))
                    s += p.get(i).get(j);
                else if(j == p.get(i).size()-1)
                    s += p.get(i).get(j);
                else
                    s += (p.get(i).get(j)+" ");
            }
            goal_sentences.add(s);
        }
        BinaryExpTree b = new BinaryExpTree(kb);
        BinaryExpTree b1 = new BinaryExpTree(p);
        ArrayList<Node> bTrees = b.getTrees();
        ArrayList<Node> proofTrees = b1.getTrees();
        for (Node n : bTrees) {
            b.convertCnf(n);
            b.removeDuplicateClauses(n);
        }
        ArrayList<Node> proofCNF = new ArrayList<>();
        for (Node n : proofTrees) {
            Node not = new Node("~",true);
            not.left = n;
            n.parent = not;
            proofCNF.add(b1.convertCnf(not));
        }
        for (Node n : proofCNF)
            b1.removeDuplicateClauses(n);
        LinkedList<Clause> kbClauses = new LinkedList<>();

        System.out.println("knowledge base clauses:\n");
        for (int i = 0; i < bTrees.size(); i++) {
            for (int j = 0; j < bTrees.get(i).cnf.clauses.size(); j++) {
                kbClauses.add(bTrees.get(i).cnf.clauses.get(j));
                bTrees.get(i).cnf.clauses.get(j).toString();
            }
            System.out.println("");
        }
        for (int i = 0; i < proofCNF.size(); i++) {
            System.out.println("****************");
            System.out.println("Goal Sentence " + (i+1) +":\n");
            System.out.println(goal_sentences.get(i));
            System.out.println("****************\n");
            System.out.println("Negated goal in clauses:\n");
            proofCNF.get(i).toString();
            System.out.println("");
            System.out.println("Proof by refutation:\n");
            PLResolution pl_resolution = new PLResolution(kbClauses, proofCNF.get(i).cnf.clauses, goal_sentences.get(i));
            pl_resolution.resolution();
        }

        System.out.println();
    }
}
