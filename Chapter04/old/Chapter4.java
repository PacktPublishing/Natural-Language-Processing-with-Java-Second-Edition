package packt;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.eval.FMeasure;

public class Chapter4 {

    private static final String sentences[] = {"Joe was the last person to see Fred. ",
        "He saw him in Boston at McKenzie's pub at 3:00 where he paid "
        + "$2.45 for an ale. ",
        "Joe wanted to go to Vermont for the day to visit a cousin who "
        + "works at IBM, but Sally and he had to look for Fred"};

    private static String regularExpressionText
            = "He left his email address (rgb@colorworks.com) and his "
            + "phone number,800-555-1234. We believe his current address "
            + "is 100 Washington Place, Seattle, CO 12345-1234. I "
            + "understand you can also call at 123-555-1234 between "
            + "8:00 AM and 4:30 most days. His URL is http://example.com "
            + "and he was born on February 25, 1954 or 2/25/1954.";

    private static MapDictionary<String> dictionary;

    public static void main(String[] args) {
        usingRegularExpressions();
//        usingOpenNLP();
//        usingStanfordNER();
//        usingLingPipeNER();
//        trainingOpenNLPNERModel();
    }

    public static File getModelDir() {
        return new File("C:\\Current Books in Progress\\NLP and Java\\Models");
    }

    private static void usingRegularExpressions() {
        usingJavaRegularExpressions();
//        usingLingPipeRegExChunker();
//        usingLingPipeRegularExpressions();
    }

    private static void usingJavaRegularExpressions() {
        String phoneNumberRE = "\\d{3}-\\d{3}-\\d{4}";
        String urlRegex = "\\b(https?|ftp|file|ldap)://"
                + "[-A-Za-z0-9+&@#/%?=~_|!:,.;]"
                + "*[-A-Za-z0-9+&@#/%=~_|]";
        String zipCodeRegEx = "[0-9]{5}(\\-?[0-9]{4})?";
        String emailRegEx = "[a-zA-Z0-9'._%+-]+@"
                + "(?:[a-zA-Z0-9-]+\\.)"
                + "+[a-zA-Z]{2,4}";
        String timeRE = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        String dateRE = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\\\d\\\\d)";
        dateRE = "((0?[13578]|10|12)(-|\\/)(([1-9])|(0[1-9])|([12])([0-9]?)|(3[01]?))(-|\\/)((19)([2-9])(\\d{1})|(20)([01])(\\d{1})|([8901])(\\d{1}))|(0?[2469]|11)(-|\\/)(([1-9])|(0[1-9])|([12])([0-9]?)|(3[0]?))(-|\\/)((19)([2-9])(\\d{1})|(20)([01])(\\d{1})|([8901])(\\d{1})))";
        Pattern pattern = Pattern.compile(phoneNumberRE + "|" + timeRE + "|" + emailRegEx);
//        regularExpressionText = "(888)555-1234 888-SEL-HIGH 888-555-1234-J88-W3S";
        Matcher matcher = pattern.matcher(regularExpressionText);
        System.out.println("---Searching ...");
        while (matcher.find()) {
            System.out.println(matcher.group() + " [" + matcher.start()
                    + ":" + matcher.end() + "]");
        }
        System.out.println("---Done Searching ...");

    }

    private static void usingLingPipeRegExChunker() {
        String timeRE = "(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?";
        Chunker chunker = new TimeRegexChunker();
//        chunker = new RegExChunker(timeRE,"time",1.0);
        Chunking chunking = chunker.chunk(regularExpressionText);
        Set<Chunk> chunkSet = chunking.chunkSet();
        displayChunkSet(chunker, regularExpressionText);
    }

    private static void usingLingPipeRegularExpressions() {
        try {
            File modelFile = new File(getModelDir(),
                    "ne-en-news-muc6.AbstractCharLmRescoringChunker");
            Chunker chunker = (Chunker) AbstractExternalizable.readObject(modelFile);
            for (int i = 0; i < sentences.length; ++i) {
                Chunking chunking = chunker.chunk(sentences[i]);
                System.out.println("Chunking=" + chunking);
            }
            for (String sentence : sentences) {
                displayChunkSet(chunker, sentence);
            }

        } catch (IOException | ClassNotFoundException ex) {
            // Handle exception
        }

    }

    // ------ OpenNLP-----------------------------------
    private static void usingOpenNLP() {
        System.out.println("OpenNLP Examples");
        usingOpenNLPNameFinderME();
//        usingMultipleNERModels();
    }

    private static void usingOpenNLPNameFinderME() {
        System.out.println("OpenNLP NameFinderME Examples");
        try (InputStream tokenStream = new FileInputStream(
                new File(getModelDir(), "en-token.bin"));
                InputStream modelStream = new FileInputStream(
                        new File(getModelDir(), "en-ner-person.bin"));) {

            TokenizerModel tokenModel = new TokenizerModel(tokenStream);
            Tokenizer tokenizer = new TokenizerME(tokenModel);

            TokenNameFinderModel entityModel
                    = new TokenNameFinderModel(modelStream);
            NameFinderME nameFinder = new NameFinderME(entityModel);

            // Single sentence
            {
                System.out.println("Single sentence");
                StringBuilder builder = new StringBuilder();
                String sentence = "He was the last person to see Fred.";

                String tokens[] = tokenizer.tokenize(sentence);
                Span nameSpans[] = nameFinder.find(tokens);

                for (int i = 0; i < nameSpans.length; i++) {
                    System.out.println("Span: " + nameSpans[i].toString());
                    System.out.println("Entity: "
                            + tokens[nameSpans[i].getStart()]);
                }
            }
            System.out.println();
            for (String sentence : sentences) {
                String tokens[] = tokenizer.tokenize(sentence);
                Span nameSpans[] = nameFinder.find(tokens);
                double[] spanProbs = nameFinder.probs(nameSpans);

                for (int i = 0; i < nameSpans.length; i++) {
                    System.out.println("Span: " + nameSpans[i].toString());
                    System.out.println("Entity: "
                            + tokens[nameSpans[i].getStart()]);
                    System.out.println("Probability: " + spanProbs[i]);
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void usingMultipleNERModels() {
        // Models - en-ner-person.bin en-ner-location.bin en-ner-money.bin 
        // en-ner-organization.bin en-ner-time.bin
        try {
            InputStream tokenStream = new FileInputStream(
                    new File(getModelDir(), "en-token.bin"));

            TokenizerModel tokenModel = new TokenizerModel(tokenStream);
            Tokenizer tokenizer = new TokenizerME(tokenModel);

            String modelNames[] = {"en-ner-person.bin", "en-ner-location.bin",
                "en-ner-organization.bin"};
            ArrayList<String> list = new ArrayList();
            for (String name : modelNames) {
                TokenNameFinderModel entityModel = new TokenNameFinderModel(
                        new FileInputStream(
                                new File(getModelDir(), name)));
                NameFinderME nameFinder = new NameFinderME(entityModel);
                for (int index = 0; index < sentences.length; index++) {
                    String tokens[] = tokenizer.tokenize(sentences[index]);
                    Span nameSpans[] = nameFinder.find(tokens);
                    for (Span span : nameSpans) {
                        list.add("Sentence: " + index
                                + " Span: " + span.toString() + " Entity: "
                                + tokens[span.getStart()]);
                    }
                }
            }
            System.out.println("Multiple Entities");
            for (String element : list) {
                System.out.println(element);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void usingStanfordNER() {
        String model = getModelDir() + "\\english.conll.4class.distsim.crf.ser.gz";
        CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(model);

        String sentence = "";
        for (String element : sentences) {
            sentence += element;
        }

        List<List<CoreLabel>> entityList = classifier.classify(sentence);

        for (List<CoreLabel> internalList : entityList) {
            for (CoreLabel coreLabel : internalList) {
                String word = coreLabel.word();
                String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
//                System.out.println(word + ":" + category);
                if (!"O".equals(category)) {
                    System.out.println(word + ":" + category);
                }

            }

        }
    }

    private static void usingLingPipeNER() {
//        usingLingPipeRexExChunker();
        usingExactDictionaryChunker();

    }

    private static void usingLingPipeRexExChunker() {
        try {
            File modelFile = new File(getModelDir(),
                    "ne-en-news-muc6.AbstractCharLmRescoringChunker");
            Chunker chunker
                    = (Chunker) AbstractExternalizable.readObject(modelFile);

            for (String sentence : sentences) {
                displayChunkSet(chunker, sentence);
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void displayChunkSet(Chunker chunker, String text) {
        Chunking chunking = chunker.chunk(text);
        Set<Chunk> set = chunking.chunkSet();
        for (Chunk chunk : set) {
            System.out.println("Type: " + chunk.type() + " Entity: ["
                    + text.substring(chunk.start(), chunk.end())
                    + "] Score: " + chunk.score());
        }
    }

    private static void initializeDictionary() {
        dictionary = new MapDictionary<String>();
        dictionary.addEntry(
                new DictionaryEntry<String>("Joe", "PERSON", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("Fred", "PERSON", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("Boston", "PLACE", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("pub", "PLACE", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("Vermont", "PLACE", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("IBM", "ORGANIZATION", 1.0));
        dictionary.addEntry(
                new DictionaryEntry<String>("Sally", "PERSON", 1.0));
    }

    private static void usingExactDictionaryChunker() {
        initializeDictionary();
        System.out.println("\nDICTIONARY\n" + dictionary);

        ExactDictionaryChunker dictionaryChunker
                = new ExactDictionaryChunker(dictionary,
                        IndoEuropeanTokenizerFactory.INSTANCE, true, false);

        for (String sentence : sentences) {
            System.out.println("\nTEXT=" + sentence);
            displayChunkSet(dictionaryChunker, sentence);
        }
    }

    // Training Models
    private static void trainingOpenNLPNERModel() {
        try (OutputStream modelOutputStream = new BufferedOutputStream(
                new FileOutputStream(new File("modelFile")));) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    new FileInputStream("en-ner-person.train"), "UTF-8");
            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);

            TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream,
                    null, 100, 5);

            model.serialize(modelOutputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
