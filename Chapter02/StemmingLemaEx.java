/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;

/**
 *
 * @author ashish
 */
public class StemmingLemaEx {
    public static void main(String args[]){
        String words[] = {"bank", "banking", "banks", "banker", "banked", 
    "bankart"};
        PorterStemmer ps = new PorterStemmer();
        for(String w : words){
            String stem = ps.stem(w);
            System.out.println("Word : " + w + " Stem : " + stem);
        }
        String paragraph = "When determining the end of sentences "
            + "we need to consider several factors. Sentences may end with "
            + "exclamation marks! Or possibly questions marks? Within "
            + "sentences we may find numbers like 3.14159, abbreviations "
            + "such as found in Mr. Smith, and possibly ellipses either "
            + "within a sentence …, or at the end of a sentence…";
        String simple = "[.?!]";
        String[] splitString = (paragraph.split(simple));
        for (String string : splitString) {
            System.out.println(string);
        }
        System.out.println("-------------Using Pattern and Matcher-------------");
        Pattern sentencePattern = Pattern.compile(
            "# Match a sentence ending in punctuation or EOS.\n"
            + "[^.!?\\s]    # First char is non-punct, non-ws\n"
            + "[^.!?]*      # Greedily consume up to punctuation.\n"
            + "(?:          # Group for unrolling the loop.\n"
            + "  [.!?]      # (special) inner punctuation ok if\n"
            + "  (?!['\"]?\\s|$)  # not followed by ws or EOS.\n"
            + "  [^.!?]*    # Greedily consume up to punctuation.\n"
            + ")*           # Zero or more (special normal*)\n"
            + "[.!?]?       # Optional ending punctuation.\n"
            + "['\"]?       # Optional closing quote.\n"
            + "(?=\\s|$)",
            Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher matcher = sentencePattern.matcher(paragraph);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
        System.out.println("-------------Using BreakIterator-------------");
        BreakIterator si = BreakIterator.getSentenceInstance();
        Locale cl = new Locale("en", "US");
        si.setText(paragraph);
        int boundary = si.first();
        while(boundary!=BreakIterator.DONE){
            int begin = boundary;
            System.out.println(boundary + " - ");
            boundary = si.next();
            int end = boundary;
            if(end == BreakIterator.DONE){
                break;
            }
            System.out.println(boundary + " [ " + paragraph.substring(begin,end) + " ] ");
        }
        System.out.println("-------------Using SentenceDetectorME-------------");
        try{
            InputStream is = new FileInputStream(new File("/home/ashish/Downloads/" + "en-sent.bin"));
            SentenceModel sm = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(sm);
            String sentences [] = detector.sentDetect(paragraph);
            for(String s : sentences){
                System.out.println(s);
            }
        }
        catch(IOException e){
            System.out.println("Error Detected" + e);
            e.printStackTrace();
        }
    }
    
}
