/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter6;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.StringList;

/**
 *
 * @author ashish
 */
public class NGramTest {
    public static void main(String args[]){
        String sampletext = "This is n-gram model";
        System.out.println(sampletext);
        
        StringList tokens = new StringList(WhitespaceTokenizer.INSTANCE.tokenize(sampletext));
        System.out.println("Tokens " + tokens);
        
        NGramModel nGramModel = new NGramModel();
        nGramModel.add(tokens,2,4); // minlength and maxlength
        
        System.out.println("Total ngrams: " + nGramModel.numberOfGrams());
        for (StringList ngram : nGramModel) {
            System.out.println(nGramModel.getCount(ngram) + " - " + ngram);
        }
    }
    
}
