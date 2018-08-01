/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter10;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import static org.jdom2.filter.Filters.document;

/**
 *
 * @author ashish
 */
public class CoreferenceDemo {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter10/";
        return resourcePath;
    }
    public static void main(String args[]){
        String sentence = "He took his cash and she took her change "  
            + "and together they bought their lunch."; 
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
//        Map<Integer, CorefChain> corefChainMap =  annotation.get(CorefChainAnnotation.class); 

        Map<Integer, CorefChain> corefChainMap = annotation.get(CorefChainAnnotation.class);
        Set<Integer> set = corefChainMap.keySet(); 
        Iterator<Integer> setIterator = set.iterator(); 
        while(setIterator.hasNext()) { 
            CorefChain corefChain = corefChainMap.get(setIterator.next()); 
            System.out.println("CorefChain: " + corefChain); 
            System.out.print("ClusterId: " + corefChain.getChainID()); 
            CorefMention mention = corefChain.getRepresentativeMention(); 
            System.out.println(" CorefMention: " + mention  
                + " Span: [" + mention.mentionSpan + "]"); 

            List<CorefMention> mentionList =  
                corefChain.getMentionsInTextualOrder(); 
            Iterator<CorefMention> mentionIterator =  
                mentionList.iterator(); 
            while(mentionIterator.hasNext()) { 
                CorefMention cfm = mentionIterator.next(); 
                System.out.println("tMention: " + cfm  
                    + " Span: [" + mention.mentionSpan + "]"); 
                System.out.print("tMention Mention Type: "  
                    + cfm.mentionType + " Gender: " + cfm.gender); 
                System.out.println(" Start: " + cfm.startIndex  
                    + " End: " + cfm.endIndex); 
            } 
            System.out.println(); 
        } 
        
        
        
    }
}
