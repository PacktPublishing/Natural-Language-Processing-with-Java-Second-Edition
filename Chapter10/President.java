/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter10;

import edu.stanford.nlp.trees.TypedDependency;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import opennlp.tools.tokenize.SimpleTokenizer;

/**
 *
 * @author ashish
 */
public class President {
    private String name; 
    private int start; 
    private int end; 
 
    public President(){
        
    }
    public President(String name, int start, int end) { 
        this.name = name; 
        this.start = start; 
        this.end = end; 
    } 

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
    
    
    private static int getOrder(String position) { 
        String tmp = ""; 
        int i = 0; 
        while (Character.isDigit(position.charAt(i))) { 
            tmp += position.charAt(i++); 
        } 
        return Integer.parseInt(tmp); 
    } 
    
    public void processWhoQuestion(List<TypedDependency> tdl) { 
    List<President> list = createPresidentList(); 
    for (TypedDependency dependency : tdl) { 
        if ("president".equalsIgnoreCase( 
                dependency.gov().originalText()) 
                && "adjectival modifier".equals( 
                  dependency.reln().getLongName())) { 
            String positionText =  
                dependency.dep().originalText(); 
            int position = getOrder(positionText)-1; 
            System.out.println("The president is "  
                + list.get(position).getName()); 
         } 
        } 
    } 
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter10/";
        return resourcePath;
    }
    public List<President> createPresidentList() { 
    ArrayList<President> list = new ArrayList<>(); 
    String line = null; 
    try (FileReader reader = new FileReader(getResourcePath() + "PresidentList"); 
            BufferedReader br = new BufferedReader(reader)) { 
        while ((line = br.readLine()) != null) { 
            System.out.println(">>>>>>>>>>>>." + line);
            SimpleTokenizer simpleTokenizer =  
                SimpleTokenizer.INSTANCE; 
            String tokens[] = simpleTokenizer.tokenize(line); 
            for(int i=0;i<tokens.length;i++)
                System.out.println(tokens[i]);
            String name = ""; 
            String start = ""; 
            String end = ""; 
            int i = 0; 
            while (!tokens[i].equals("(")) { 
                name += tokens[i] + " "; 
                i++; 
            } 
            start = tokens[i + 1]; 
            end = tokens[i + 3]; 
            if (end.equalsIgnoreCase("present")) { 
                end = start; 
            } 
            list.add(new President(name,  
                Integer.parseInt(start), 
                Integer.parseInt(end))); 
        } 
     } catch (IOException ex) { 
        // Handle exceptions 
    } 
    return list; 
} 
}
