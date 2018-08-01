/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ashish
 */
public class SBDDemo {
    private static String paragraph = "When determining the end of sentences "
    + "we need to consider several factors. Sentences may end with "
    + "exclamation marks! Or possibly questions marks? Within "
    + "sentences we may find numbers like 3.14159, abbreviations "
    + "such as found in Mr. Smith, and possibly ellipses either "
    + "within a sentence …, or at the end of a sentence…";
    
    public static void main(String args[]){
        System.out.println("--------- Simple regex ---------");
        String simple = "[.?!]";
        String[] splitString = (paragraph.split(simple));
        for (String string : splitString) {
            System.out.println(string);
        }
        System.out.println(">>>> Using Pattern and Matcher --------");
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
    }
}
