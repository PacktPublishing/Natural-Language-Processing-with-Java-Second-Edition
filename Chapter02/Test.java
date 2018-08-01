/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter2;

import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.DocumentProcessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.Tokenizer;
//import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 *
 * @author ashish
 */
public class Test {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter2/";
        return resourcePath;
    }
    public static void main(String args[]){
        Scanner s = new Scanner("Let's pause, and then reflect");
        s.useDelimiter("[,.]");
        List<String> l = new ArrayList<>();
        while(s.hasNext()){
            String token = s.next();
            l.add(token);
        }
        for(String token : l){
            System.out.println(token);
        }
        String text = "Mr. Smith went to 123 Washington avenue";
        String tokens[] = text.split("\\s+");
        for(String token: tokens){
            System.out.println(token);
        }
        BreakIterator b = BreakIterator.getWordInstance();
        text = "Let's pause, and then reflect";
        b.setText(text);
        int boundary = b.first();
        while(boundary!=BreakIterator.DONE){
            int begin = boundary;
            System.out.println(boundary);
            boundary = b.next();
            int end = boundary;
            if(end==BreakIterator.DONE){
                break;
            }
            System.out.println(boundary + "[" + text.substring(begin,end) + "]");
        }
        
        try{
            StreamTokenizer t = new StreamTokenizer(
                    new StringReader("Let's pause, and then reflect."));
            boolean isEOF = false;
            while(!isEOF){
                int token = t.nextToken();
                switch(token){
                    case StreamTokenizer.TT_EOF:
                        isEOF = true;
                        break;
                    case StreamTokenizer.TT_EOL:
                        break;
                    case StreamTokenizer.TT_WORD:
                        System.out.println(t.sval);
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.println(t.nval);
                        break;
                    default:
                        System.out.println((char)token);
                }
            }
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        // Using OpenNLP
        
        String paragraph = "Let's pause, \nand then reflect.";
        SimpleTokenizer simpletokenizer = SimpleTokenizer.INSTANCE;
        String simpletokens[] = simpletokenizer.tokenize(paragraph);
        for(String token : simpletokens){
            System.out.println(token);
        }
        
        tokens = WhitespaceTokenizer.INSTANCE.tokenize(paragraph);
        for (String token : tokens) {
            System.out.println(token);
        }
        
        try
        {
            InputStream modelis = new FileInputStream(new File(getResourcePath() + "en-token.bin"));
            TokenizerModel model = new TokenizerModel(modelis);
            Tokenizer tokenizer = new TokenizerME(model);
            tokens= tokenizer.tokenize(paragraph);
            for (String token : tokens){
                System.out.println(token);    
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        
        
        PTBTokenizer ptb = new PTBTokenizer(new StringReader(paragraph), new WordTokenFactory(),null);
        while(ptb.hasNext()){
            System.out.println(ptb.next());
        }
        
        CoreLabelTokenFactory ctf = new CoreLabelTokenFactory();
        ptb = new PTBTokenizer(new StringReader(paragraph), ctf, "invertible=true");
        while(ptb.hasNext()){
           CoreLabel cl = (CoreLabel)ptb.next();
            System.out.println(cl.originalText() + "(" + cl.beginPosition() + "-" + cl.endPosition() + ")" );
        }
        
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        Iterator<List<HasWord>> it = dp.iterator();
        while(it.hasNext()){
            List<HasWord> sentence = it.next();
            for(HasWord token : sentence){
                System.out.println(token);
            }
        }
        Properties prop = new Properties();
        prop.put("annonators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);
//        Annotation ann = new Annotation(paragraph);
        Annotation ann = new Annotation(paragraph);
        pipeline.annotate(ann);
        pipeline.prettyPrint(ann, System.out);
        
        // LingPipe Tokenizers
        char texts[] = paragraph.toCharArray();
        
        TokenizerFactory tfac = IndoEuropeanTokenizerFactory.INSTANCE;
        com.aliasi.tokenizer.Tokenizer tokens1 = tfac.tokenizer(texts, 0, texts.length);
        for(String t : tokens1){
            System.out.println(t);
        }
        
        BufferedOutputStream bos = null;
        try{
            ObjectStream<String> linestream = new PlainTextByLineStream((InputStreamFactory) new FileInputStream("training.train"),"UTF-8");
            ObjectStream<TokenSample> samplestream = new TokenSampleStream(linestream);
//            TokenizerModel model = TokenizerME.train(samplestream, factory, mlParams)
            
        }
        catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        
    }
    
}
