package edu.iastate.cs472.proj2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Reader {
    ArrayList<ArrayList<String>> kb_sentences;
    ArrayList<ArrayList<String>> proofs;
    File file;

    public Reader(File f) {
        file = f;
        kb_sentences = new ArrayList<ArrayList<String>>();
        proofs  = new ArrayList<ArrayList<String>>();
    }

    public ArrayList<ArrayList<String>> getKbSenteces() throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        String line;
        while(sc.hasNext() == true && !((line = sc.nextLine()).matches("Prove the following sentences by refutation:"))) {
            if(line.matches("Knowledge Base:") || line.matches(""))
                continue;
            String[] delim = line.split(" ");
            ArrayList<String> temp = new ArrayList<String>();
            for (String s: delim) {
                if(s.charAt(0) == '~') {
                    temp.add("~");
                    temp.add(s.substring(1));
                }
                else
                    temp.add(s);
            }
            kb_sentences.add(temp);
        }
        return kb_sentences;
    }

    public ArrayList<ArrayList<String>> getProofs() throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        String line = "";
        while (sc.hasNext() && !(line = sc.nextLine()).matches("Prove the following sentences by refutation:"))
            continue;
        while(sc.hasNext() == true) {
            line = sc.nextLine();
            if(line.matches("Prove the following sentences by refutation:") || line.matches(""))
                continue;
            String[] delim = line.split(" ");
            ArrayList<String> temp = new ArrayList<String>();
            for (String s: delim) {
                if(s.charAt(0) == '~') {
                    temp.add("~");
                    temp.add(s.substring(1));
                }
                else
                    temp.add(s);
            }
            proofs.add(temp);
        }
        return proofs;
    }
}
