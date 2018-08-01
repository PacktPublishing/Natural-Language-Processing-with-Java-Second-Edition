/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter10;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

/**
 *
 * @author ashish
 */
public class DemoParsing {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter10/";
        return resourcePath;
    }
    public static void main(String args[]){
        String fileLocation = getResourcePath() + "en-parser-chunking.bin";
        try {
            InputStream modelInputStream = new FileInputStream(fileLocation);
            ParserModel model = new ParserModel(modelInputStream);
            Parser parser = ParserFactory.create(model);
            String sentence = "The cow jumped over the moon"; 
            Parse parses[] = ParserTool.parseLine(sentence, parser, 3); 
            for(Parse parse : parses) { 
                parse.show(); 
                System.out.println("Probability: " + parse.getProb()); 
                parse.showCodeTree();
            
                Parse children[] = parse.getChildren(); 
                for (Parse parseElement : children) { 
                    System.out.println(parseElement.getText()); 
                    System.out.println(parseElement.getType()); 
                    Parse tags[] = parseElement.getTagNodes(); 
                    System.out.println("Tags"); 
                    for (Parse tag : tags) { 
                        System.out.println("[" + tag + "]"  
                            + " type: " + tag.getType()  
                            + "  Probability: " + tag.getProb()  
                            + "  Label: " + tag.getLabel()); 
                    } 
                }
            } 
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
