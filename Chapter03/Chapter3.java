package packt;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.trees.SimpleTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.sentdetect.SentenceDetectorEvaluator;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class Chapter3 {

    private static String paragraph = "When determining the end of sentences "
            + "we need to consider several factors. Sentences may end with "
            + "exclamation marks! Or possibly questions marks? Within "
            + "sentences we may find numbers like 3.14159, abbreviations "
            + "such as found in Mr. Smith, and possibly ellipses either "
            + "within a sentence …, or at the end of a sentence….";

    public static void main(String[] args) {
//        usingRegularExpressions();
//        usingBreakIterator();

//        usingOpenNLP();
//        usingOpenNLPSentPosDetectMethod();
//        usingStanfordSentenceDetectorExamples();
        useLingPipeExamples();
//        usingOpenNLPSentenceTrainer();
    }

    private static void usingRegularExpressions() {
        String EXAMPLE_TEST = "This is my small example. "
                + "This is going to match!  Or will it? "
                + "Otherwise, does it work for a number like 4.23?";
        String pattern = "((?<=[a-z0-9][.?!])|(?<=[a-z0-9][.?!]\\\"))(\\s|\\r\\n)(?=\\\"?[A-Z])";
        String firstPart = "(?<=[a-z0-9][.?!])";
        String secondPart = "(\\s|\\r\\n)(?=\\\"?[A-Z])";
        String regex = "^\\s+[A-Za-z,;'\"\\s]+[.?!]$";

        String simple = "[.?!]";
        String[] splitString = (paragraph.split(simple));
        for (String string : splitString) {
            System.out.println(string);
        }

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

    private static void test(String subjectString) {

    }

    public static void usingBreakIterator() {
        Locale currentLocale = new Locale("en", "US");
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance();
        sentenceIterator.setText(paragraph);
        int boundary = sentenceIterator.first();
        while (boundary != BreakIterator.DONE) {
            int begin = boundary;
            System.out.print(boundary + "-");
            boundary = sentenceIterator.next();
            int end = boundary;
            if (end == BreakIterator.DONE) {
                break;
            }
            System.out.println(boundary + " ["
                    + paragraph.substring(begin, end) + "]");
        }
    }

    public static File getModelDir() {
        return new File("C:\\OpenNLP Models");
    }

    private static void usingOpenNLP() {
        try (InputStream is = new FileInputStream(new File(
                getModelDir(), "en-sent.bin"))) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
//            paragraph = "  This sentence starts with spaces and ends with " +
//                    "spaces  . This sentence has no spaces between the next " +
//                    "one.This is the next one.";
            String sentences[] = detector.sentDetect(paragraph);
            for (String sentence : sentences) {
                System.out.println(sentence);
            }
            double probablities[] = detector.getSentenceProbabilities();
            for (double probablity : probablities) {
                System.out.println(probablity);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

	
    private static void usingOpenNLPSentPosDetectMethod() {
        try (InputStream is = new FileInputStream(new File(
                getModelDir(), "en-sent.bin"))) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
//            paragraph = "  This sentence starts with spaces and ends with " +
//                    "spaces  . This sentence has no spaces between the next " +
//                    "one.This is the next one.";
            Span spans[] = detector.sentPosDetect(paragraph);
            for (Span span : spans) {
                System.out.println(span);
            }
            for (Span span : spans) {
                System.out.println(span + "["
                        + paragraph.substring(span.getStart(), span.getEnd())
                        + "]");
            }
            for (Span span : spans) {
                System.out.println(span.getType());
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void usingOpenNLPSentenceDetectorFactory() {
        // Used how??
        try (InputStream is = new FileInputStream(new File(
                getModelDir(), "en-sent.bin"))) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
//            paragraph = "  This sentence starts with spaces and ends with " +
//                    "spaces  . This sentence has no spaces between the next " +
//                    "one.This is the next one.";
            String sentences[] = detector.sentDetect(paragraph);
            for (String sentence : sentences) {
                System.out.println(sentence);
            }
            double probablities[] = detector.getSentenceProbabilities();
            for (double probablity : probablities) {
                System.out.println(probablity);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ---- Stanford Examples ----------------------------------------------
    private static void usingStanfordSentenceDetectorExamples() {
        System.out.println("--- usingStanfordSentenceDetectorExamples ----");
//        usingStanfordPTBTokenizer();
//        usingStanfordDocumentPreprocessor();
        usingStanfordPipeline();
    }

    private static void usingStanfordPTBTokenizer() {
        PTBTokenizer ptb = new PTBTokenizer(
                new StringReader(paragraph), new CoreLabelTokenFactory(), null);
        WordToSentenceProcessor wtsp = new WordToSentenceProcessor();
        List<List<CoreLabel>> sents = wtsp.process(ptb.tokenize());
//        List<String> sentences = new ArrayList<>();
        for (List<CoreLabel> sent : sents) {
            System.out.println(sent);
        }
        for (List<CoreLabel> sent : sents) {
            for (CoreLabel element : sent) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
//            System.out.println("Size: " + sent.size());
//            System.out.print("Last: " + sent.get(sent.size() - 1).endPosition());
//            System.out.println(" - " + sent.get(sent.size() - 1));
        for (List<CoreLabel> sent : sents) {
            for (CoreLabel element : sent) {
                System.out.print(element.endPosition() + " "); 
            }
            System.out.println();
        }
//      for (List<CoreLabel> sent : sents) {
 //             System.out.println(sent.get(0) + " "
 //           + sent.get(0).beginPosition());
//      }
        // sing options
        paragraph = "The colour of money is green. Common fraction "
                + "characters such as ½  are converted to the long form 1/2. "
                + "Quotes such as “cat” are converted to their simpler form.";
        ptb = new PTBTokenizer(
                new StringReader(paragraph), new CoreLabelTokenFactory(),
                "americanize=true,normalizeFractions=true,asciiQuotes=true");
        wtsp = new WordToSentenceProcessor();
        sents = wtsp.process(ptb.tokenize());
        for (List<CoreLabel> sent : sents) {
            for (CoreLabel element : sent) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    private static void usingStanfordDocumentPreprocessor() {
        // option #1: By sentence.
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List sentence : dp) {
            System.out.println(sentence);
        }
		// try {
        //   Reader reader = new FileReader("XMLText.xml");
        //   DocumentPreprocessor dp = new DocumentPreprocessor(
        //   reader, DocumentPreprocessor.DocType.XML);
        //   dp.setElementDelimiter("sentence");
        //   for (List sentence : dp) {
        //   System.out.println(sentence);
        //   }
        //} catch (FileNotFoundException ex) {
        // Handle exception
        //}

//        // option #2: By token
//        PTBTokenizer ptbt = new PTBTokenizer(reader,
//                new CoreLabelTokenFactory(), "");
//        CoreLabel label;
//        while (ptbt.hasNext()) {
//            System.out.println(ptbt.next());
//        }    
    }

    private static void usingStanfordPipeline() {
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(paragraph);
        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);
//        try {
//            pipeline.xmlPrint(annotation, System.out);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        Annotation a = pipeline.process(paragraph);
        System.out.println("----------");
        System.out.println(a.size());
        System.out.println("----------");
        System.out.println(annotation);
        System.out.println("----------");
        System.out.println(annotation.toShorterString("NN"));
//        TreePrint treePrint = pipeline.getConstituentTreePrinter();
//        treePrint = pipeline.getDependencyTreePrinter();
//        treePrint.printTree(new SimpleTree());
    }

    // ---- LingPipe Examples ----------------------------------------------
    private static void useLingPipeExamples() {
//        useLingPipeIndoEuropeanModelExamples();
        useLingPipeMedLineModelExamples();
    }

    private static void useLingPipeIndoEuropeanModelExamples() {
        TokenizerFactory TOKENIZER_FACTORY
                = IndoEuropeanTokenizerFactory.INSTANCE;
        com.aliasi.sentences.SentenceModel sentenceModel = new IndoEuropeanSentenceModel();

        List<String> tokenList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        Tokenizer tokenizer
                = TOKENIZER_FACTORY.tokenizer(paragraph.toCharArray(),
                        0, paragraph.length());
        tokenizer.tokenize(tokenList, whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries
                = sentenceModel.boundaryIndices(tokens, whites);
//        System.out.println(tokenList.size());
//        System.out.println(whiteList.size());
//        System.out.println("[" + whiteList.get(0) + "]");
        for (int boundary : sentenceBoundaries) {
            System.out.println(boundary);
        }
        int start = 0;
        for (int boundary : sentenceBoundaries) {
            while (start <= boundary) {
                System.out.print(tokenList.get(start) + whiteList.get(start + 1));
                start++;
            }
            System.out.println();
        }

    }

    private static void useLingPipeMedLineModelExamples() {
        // From: http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3139422/
//        paragraph = "HepG2 cells were obtained from the American Type Culture Collection (Rockville, MD, USA) and were used only until passage 30. They were routinely grown at 37°C in Dulbecco’s modified Eagle’s medium (DMEM) containing 10 % fetal bovine serum (FBS), 2 mM glutamine, 1 mM sodium pyruvate, and 25 mM glucose (Invitrogen, Carlsbad, CA, USA) in a humidified atmosphere containing 5% CO2. For precursor and 13C-sugar experiments, tissue culture treated polystyrene 35 mm dishes (Corning Inc, Lowell, MA, USA) were seeded with 2 × 106 cells and grown to confluency in DMEM.";

        TokenizerFactory tokenizerfactory
                = IndoEuropeanTokenizerFactory.INSTANCE;
//        MedlineSentenceModel sentenceModel
//                = new MedlineSentenceModel();
        com.aliasi.sentences.SentenceModel sentenceModel
                = new IndoEuropeanSentenceModel();
        SentenceChunker sentenceChunker = new SentenceChunker(tokenizerfactory,
                sentenceModel);

        Chunking chunking = sentenceChunker.chunk(
                paragraph.toCharArray(),0, paragraph.length());
        Set<Chunk> sentences = chunking.chunkSet();
        String slice = chunking.charSequence().toString();

//        int i = 1;
        for (Chunk sentence : sentences) {
//            int start = sentence.start();
//            int end = sentence.end();
//            System.out.println("SENTENCE " + (i++) + ":");
            System.out.println("[" + 
                    slice.substring(sentence.start(), sentence.end()) + "]");
        }

    }

    private static void usingOpenNLPSentenceTrainer() {
//        Charset charset = Charset.forName("UTF-8");
        try {
            ObjectStream<String> lineStream
                    = new PlainTextByLineStream(new FileReader("sentence.train"));
            ObjectStream<SentenceSample> sampleStream
                    = new SentenceSampleStream(lineStream);
//            SentenceDetectorFactory sdf = new SentenceDetectorFactory();
//                model = SentenceDetectorME.train("en",
//                        sampleStream,
//                        sdf,
//                        TrainingParameters.defaultParams());
            SentenceModel model = SentenceDetectorME.train(
                    "en", sampleStream, true,
                    null, TrainingParameters.defaultParams());
            OutputStream modelStream = new BufferedOutputStream(
                    new FileOutputStream("modelFile"));
            model.serialize(modelStream);
            lineStream.close();
            modelStream.close();
            sampleStream.close();

            //
            SentenceDetectorME detector = null;
            try (InputStream is = new FileInputStream(
                    new File(getModelDir(), "modelFile"))) {
                model = new SentenceModel(is);
                detector = new SentenceDetectorME(model);
                String sentences[] = detector.sentDetect(paragraph);
                for (String sentence : sentences) {
                    System.out.println(sentence);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                // Handle exception
            } catch (IOException ex) {
                ex.printStackTrace();
                // Handle exception
            }

            // Evaluate the model
            lineStream
                    = new PlainTextByLineStream(new FileReader("evalSample"));
            sampleStream
                    = new SentenceSampleStream(lineStream);
            SentenceDetectorEvaluator sentenceDetectorEvaluator
                    = new SentenceDetectorEvaluator(detector, null);
            sentenceDetectorEvaluator.evaluate(sampleStream);
            System.out.println(sentenceDetectorEvaluator.getFMeasure());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
