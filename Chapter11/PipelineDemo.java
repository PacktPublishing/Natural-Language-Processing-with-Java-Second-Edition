/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author ashish
 */
public class PipelineDemo {
    public static void main(String args[]){
        String text = "The robber took the cash and ran";
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(text);
        
        System.out.println("Before annotate method executed ");
        Set<Class<?>> annotationSet = annotation.keySet();
        for(Class c : annotationSet) {
            System.out.println("\tClass: " + c.getName());
        }

        pipeline.annotate(annotation);

        System.out.println("After annotate method executed ");
        annotationSet = annotation.keySet();
        for(Class c : annotationSet) {
            System.out.println("\tClass: " + c.getName());
        }
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class); 
                String pos = token.get(PartOfSpeechAnnotation.class); 
                System.out.println(word);
                System.out.println(pos);
            }
        }
        
        
        
        Annotation annotation1 = new Annotation("The robber took the cash and ran.");
        Annotation annotation2 = new Annotation("The policeman chased him down the street.");
        Annotation annotation3 = new Annotation("A passerby, watching the action, tripped the thief "
            + "as he passed by.");
        Annotation annotation4 = new Annotation("They all lived happily ever after, except for the thief "
            + "of course.");
        
        ArrayList<Annotation> list = new ArrayList();
        list.add(annotation1);
        list.add(annotation2);
        list.add(annotation3);
        list.add(annotation4);
        Iterable<Annotation> iterable = list;
        pipeline.annotate(iterable);
        List<CoreMap> sentences1 = annotation2.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences1) {
            for (CoreLabel token : 
                    sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println("Word: " + word + " POS Tag: " + pos);
            }
        }
    }
}
