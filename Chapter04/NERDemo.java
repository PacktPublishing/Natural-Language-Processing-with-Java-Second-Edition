/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter4;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 *
 * @author ashish
 */
public class NERDemo {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter4/";
        return resourcePath;
    }
    public static void main(String args[]){
        String sentences[] = {"Joe was the last person to see Fred. ", 
            "He saw him in Boston at McKenzie's pub at 3:00 where he " 
            + " paid $2.45 for an ale. ", 
            "Joe wanted to go to Vermont for the day to visit a cousin who " 
            + "works at IBM, but Sally and he had to look for Fred"}; 
        String sentence = "He was the last person to see Fred."; 
        try
        {
            InputStream tokenStream = new FileInputStream(new File(getResourcePath()+ "en-token.bin"));
            InputStream modelStream = new FileInputStream(new File(getResourcePath() + "en-ner-person.bin"));
            TokenizerModel tokenModel = new TokenizerModel(tokenStream);
            Tokenizer tokenizer = new TokenizerME(tokenModel);
            TokenNameFinderModel entityModel = new TokenNameFinderModel(modelStream);
            NameFinderME nameFinder = new NameFinderME(entityModel);
            String tokens1[] = tokenizer.tokenize(sentence);
            Span nameSpans1[] = nameFinder.find(tokens1);
            for (int i = 0; i < nameSpans1.length; i++) { 
                System.out.println("Span: " + nameSpans1[i].toString()); 
                System.out.println("Entity: " 
                    + tokens1[nameSpans1[i].getStart()]); 
            } 
            
            System.out.println("---------- Multiple Sentences -----------");
            for (String sentence1 : sentences) { 
                String tokens[] = tokenizer.tokenize(sentence1); 
                Span nameSpans[] = nameFinder.find(tokens); 
                for (int i = 0; i < nameSpans.length; i++) { 
                    System.out.println("Span: " + nameSpans[i].toString()); 
                    System.out.println("Entity: "  
                        + tokens[nameSpans[i].getStart()]); 
                } 
                System.out.println(); 
            } 
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
}
