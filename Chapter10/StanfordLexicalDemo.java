/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter10;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.File;
import java.io.StringReader;
import java.util.List;

/**
 *
 * @author ashish
 */
public class StanfordLexicalDemo {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter10/";
        return resourcePath;
    }
    
    public static void main(String args[]){
        String parseModel = getResourcePath() + "englishPCFG.ser.gz";
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(parseModel);
        String [] sentenceArray = {"The", "cow" ,"jumped", "over", "the", "moon", "."};
        List<CoreLabel> words = SentenceUtils.toCoreLabelList(sentenceArray);
        Tree parseTree = lexicalizedParser.apply(words); 
        parseTree.pennPrint(); 
        
        TreePrint treePrint =  new TreePrint("typedDependenciesCollapsed"); 
        treePrint.printTree(parseTree); 
        
        
        String sentence = "The cow jumped over the moon."; 
        TokenizerFactory<CoreLabel> tokenizerFactory =  PTBTokenizer.factory(new CoreLabelTokenFactory(), ""); 
        Tokenizer<CoreLabel> tokenizer =  tokenizerFactory.getTokenizer(new StringReader(sentence)); 
        List<CoreLabel> wordList = tokenizer.tokenize(); 
        parseTree = lexicalizedParser.apply(wordList); 
        TreebankLanguagePack tlp =  lexicalizedParser.treebankLanguagePack(); 
        GrammaticalStructureFactory gsf =  tlp.grammaticalStructureFactory(); 
        GrammaticalStructure gs =  gsf.newGrammaticalStructure(parseTree); 
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed(); 
        System.out.println(tdl); 
        
        for(TypedDependency dependency : tdl) { 
            System.out.println("Governor Word: [" + dependency.gov()  
                + "] Relation: [" + dependency.reln().getLongName() 
                + "] Dependent Word: [" + dependency.dep() + "]"); 
        } 
        
    }
}
