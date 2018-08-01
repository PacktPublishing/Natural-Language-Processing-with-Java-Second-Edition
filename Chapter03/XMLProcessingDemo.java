/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter3;

import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.DocumentProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class XMLProcessingDemo {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter3/XMLTest.xml";
        return resourcePath;
    }
    
    public static void main(String args[]){
        try {
            Reader reader = new FileReader(getResourcePath());
            DocumentPreprocessor dp = new DocumentPreprocessor(reader, DocumentPreprocessor.DocType.XML);
            dp.setElementDelimiter("sentence");
            for(List sentence : dp){
                ListIterator list = sentence.listIterator();
                while (list.hasNext()) { 
                    System.out.print(list.next() + " "); 
                } 
                System.out.println(); 
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLProcessingDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
