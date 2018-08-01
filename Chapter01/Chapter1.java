package packt;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

public class Chapter1 {

    public static void main(String[] args) {
//        apacheOpenNLPExample();
//        stanfordNLPExample();
        lingpipeExamples();
//        findingPartsOfText();
//        findingSentences();
//        findingPeopleAndThings();
//        nameFinderExample();        
//        detectingPartsOfSpeechExample();
//        extractingRelationshipsExample();
    }

    private static void apacheOpenNLPExample() {
        try (InputStream is = new FileInputStream(
                new File("C:\\OpenNLP Models", "en-token.bin"))) {
            TokenizerModel model = new TokenizerModel(is);
            Tokenizer tokenizer = new TokenizerME(model);
            String tokens[] = tokenizer.tokenize("He lives at 1511 W. Randolph.");
            for (String a : tokens) {
                System.out.print("[" + a + "] ");
            }
            System.out.println();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void stanfordNLPExample() {
        PTBTokenizer ptb = new PTBTokenizer(
                new StringReader("He lives at 1511 W. Randolph."),
                new CoreLabelTokenFactory(), null);
        while (ptb.hasNext()) {
            System.out.println(ptb.next());
        }

    }

    private static void lingpipeExamples() {
        List<String> tokenList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        String text = "A sample sentence processed \nby \tthe "
                + "LingPipe tokenizer.";
        com.aliasi.tokenizer.Tokenizer tokenizer = IndoEuropeanTokenizerFactory.INSTANCE.
                tokenizer(text.toCharArray(), 0, text.length());
        tokenizer.tokenize(tokenList, whiteList);
        for (String element : tokenList) {
            System.out.print(element + " ");
        }
        System.out.println();

    }

    private static void splitMethodDemonstration() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void findingPartsOfText() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void findingSentences() {
        String paragraph = "The first sentence. The second sentence.";
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor documentPreprocessor
                = new DocumentPreprocessor(reader);
        List<String> sentenceList = new LinkedList<String>();
        for (List<HasWord> element : documentPreprocessor) {
            StringBuilder sentence = new StringBuilder();
            List<HasWord> hasWordList = element;
            for (HasWord token : hasWordList) {
                sentence.append(token).append(" ");
            }
            sentenceList.add(sentence.toString());
        }
        for (String sentence : sentenceList) {
            System.out.println(sentence);
        }

    }

    private static void findingPeopleAndThings() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String target = "Washington";
        int index = text.indexOf(target);
        System.out.println(index);
    }

    private static void nameFinderExample() {
        try {
            String[] sentences = {
                "Tim was a good neighbor. Perhaps not as good a Bob "
                + "Haywood, but still pretty good. Of course Mr. Adam "
                + "took the cake!"};
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            TokenNameFinderModel model = new TokenNameFinderModel(new File(
                    "C:\\OpenNLP Models", "en-ner-person.bin"));
            NameFinderME finder = new NameFinderME(model);

            for (String sentence : sentences) {
                // Split the sentence into tokens
                String[] tokens = tokenizer.tokenize(sentence);

                // Find the names in the tokens and return Span objects
                Span[] nameSpans = finder.find(tokens);

                // Print the names extracted from the tokens using the Span data
                System.out.println(Arrays.toString(
                        Span.spansToStrings(nameSpans, tokens)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void detectingPartsOfSpeechExample() {
        String sentence = "POS processing is useful for enhancing the "
                + "quality of data sent to other elements of a pipeline.";

        POSModel model = new POSModelLoader()
                .load(new File("C:/Current Books/NLP and Java/Models/", "en-pos-maxent.bin"));
        POSTaggerME tagger = new POSTaggerME(model);

        String tokens[] = WhitespaceTokenizer.INSTANCE
                .tokenize(sentence);
        String[] tags = tagger.tag(tokens);

        POSSample sample = new POSSample(tokens, tags);
        String posTokens[] = sample.getSentence();
        String posTags[] = sample.getTags();
        for (int i = 0; i < posTokens.length; i++) {
            System.out.print(posTokens[i] + " - " + posTags[i]);
        }
        System.out.println();

        for (int i = 0; i < tokens.length; i++) {
            System.out.print(tokens[i] + "[" + tags[i] + "] ");
        }
    }

    private static void extractingRelationshipsExample() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(
                "The meaning and purpose of life is plain to see.");
        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);

    }
}
