package packt;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.didion.jwnl.JWNLException;
import opennlp.tools.coref.mention.JWNLDictionary;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Chapter2 {

    private static String paragraph = "Let's pause, \nand then reflect.";

    public static void main(String[] args) {
//        usingTheScannerClass();
//        usingTheSplitMethod();
//        usingTheBreakIterator();
//        usingTheStreamTokenizerClass();
//        usingTheStringTokenizerClass();
//        usingTheOpenNLPTokenizers();
//        usingTheStanfordTokenizer();
//        usingLingPipeTokenizers();
//        stemmerExamples();
//        usingLemmatizationExamples();
//        usingStopwordsExamples();
//        normalizationProcessExamples();
        trainingATokenizer();
    }

    private static void usingTheScannerClass() {
        Scanner scanner = new Scanner("Let's pause, and then reflect.");
        List<String> list = new ArrayList<>();
        // Specifying the delimiters
        scanner.useDelimiter("[ ,.]");

        while (scanner.hasNext()) {
            String token = scanner.next();
            list.add(token);
        }
        for (String token : list) {
            System.out.println(token);
        }
    }

    private static void usingTheSplitMethod() {
        String text = "Mr. Smith went to 123 Washington avenue.";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }

        text = "Let's pause, and then reflect.";
        tokens = text.split("[ ,.]");
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void usingTheBreakIterator() {
        Locale currentLocale = new Locale("en", "US");
        BreakIterator wordIterator
                = BreakIterator.getWordInstance();
        String text = "Let's pause, and then reflect.";
        wordIterator.setText(text);
        int boundary = wordIterator.first();
        while (boundary != BreakIterator.DONE) {
            int begin = boundary;
            System.out.print(boundary + "-");
            boundary = wordIterator.next();
            int end = boundary;
            if(end == BreakIterator.DONE) break;
            System.out.println(boundary + " [" + 
                    text.substring(begin, end) + "]");
        }
    }

    private static void usingTheStreamTokenizerClass() {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(
                    new StringReader("Let's pause, and then reflect."));
//            tokenizer.ordinaryChar('\'');
//            tokenizer.ordinaryChar(',');
            boolean isEOF = false;
            while (!isEOF) {

                int token = tokenizer.nextToken();
                switch (token) {
                    case StreamTokenizer.TT_EOF:
                        isEOF = true;
                        break;
                    case StreamTokenizer.TT_EOL:
                        break;
                    case StreamTokenizer.TT_WORD:
                        System.out.println(tokenizer.sval);
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.println(tokenizer.nval);
                        break;
                    default:
                        System.out.println((char) token);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void usingTheStringTokenizerClass() {
        StringTokenizer st
                = new StringTokenizer("Let's pause, and then reflect.");
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }

    private static void usingTheOpenNLPTokenizers() {
//        usingTheSimpleTokenizerClass();
//        usingTheSimpleTokenizerClass();
//        usingTheTokenizerMEClass();
//        usingTheWhitespaceTokenizer();
    }

    public static File getModelDir() {
        return new File("C:\\OpenNLP Models");
    }

    private static void usingTheSimpleTokenizerClass() {
        System.out.println("--- SimpleTokenizer");
        SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
        String tokens[] = simpleTokenizer.tokenize(paragraph);
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void usingTheTokenizerMEClass() {
        try {
            InputStream modelIn = new FileInputStream(new File(
                    getModelDir(), "en-token.bin"));
            TokenizerModel model = new TokenizerModel(modelIn);
            Tokenizer tokenizer = new TokenizerME(model);
            String tokens[] = tokenizer.tokenize(paragraph);
            for (String token : tokens) {
                System.out.println(token);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void usingTheWhitespaceTokenizer() {
        String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(paragraph);
        for (String token : tokens) {
            System.out.println(token);
        }
    }

    private static void stemmerExamples() {
        usingPorterStemmerExample();
//        usingTheLingPipeStemmer();
    }

    private static void usingPorterStemmerExample() {
        String words[] = {"bank", "banking", "banks", "banker",
            "banked", "bankart"};
        PorterStemmer ps = new PorterStemmer();
        for (String word : words) {
            String stem = ps.stem(word);
            System.out.println("Word: " + word + "  Stem: " + stem);
        }
    }

    private static void usingTheLingPipeStemmer() {
        String words[] = {"bank", "banking", "banks", "banker",
            "banked", "bankart"};
        TokenizerFactory tokenizerFactory
                = IndoEuropeanTokenizerFactory.INSTANCE;
        TokenizerFactory porterFactory
                = new PorterStemmerTokenizerFactory(tokenizerFactory);
        String[] stems = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            com.aliasi.tokenizer.Tokenization tokenizer
                    = new com.aliasi.tokenizer.Tokenization(words[i], porterFactory);
            stems = tokenizer.tokens();
            System.out.print("Word: " + words[i]);
            for (String stem : stems) {
                System.out.println("  Stem: " + stem);
            }
        }

    }

    private static void usingTheStanfordTokenizer() {

        // Using PTBTokenizer
        System.out.println("----PTBTokenizer Example");

        // First example
//        PTBTokenizer ptb = new PTBTokenizer(new StringReader(paragraph),
//                new CoreLabelTokenFactory(),null);
//        while (ptb.hasNext()) {
//            System.out.println(ptb.next());
//        }
        // CoreLabel example
        CoreLabelTokenFactory ctf = new CoreLabelTokenFactory();
        PTBTokenizer ptb = new PTBTokenizer(new StringReader(paragraph),
                ctf, "invertible=true");
//        PTBTokenizer ptb = new PTBTokenizer(new StringReader(paragraph),
//                new WordTokenFactory(), null);
        while (ptb.hasNext()) {
            CoreLabel cl = (CoreLabel) ptb.next();
            System.out.println(cl.originalText() + " ("
                    + cl.beginPosition() + "-" + cl.endPosition() + ")");
        }

        // Using a DocumentPreprocessor
        System.out.println("----DocumentPreprocessor Example");
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor documentPreprocessor
                = new DocumentPreprocessor(reader);

        Iterator<List<HasWord>> it = documentPreprocessor.iterator();
        while (it.hasNext()) {
            List<HasWord> sentence = it.next();
            for (HasWord token : sentence) {
                System.out.println(token);
            }
        }

//        for (List<HasWord> sentence : documentPreprocessor) {
////            List<HasWord> sentence = it.next();
//            for (HasWord token : sentence) {
//                System.out.println(token);
//            }
//        }
        // Using a pipeline
        System.out.println("----pipeline Example");
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(paragraph);

        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);

    }

    private static void usingLingPipeTokenizers() {
//        String paragraph = "sample text string";
        char text[] = paragraph.toCharArray();
        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
        com.aliasi.tokenizer.Tokenizer tokenizer = tokenizerFactory.tokenizer(
                text, 0, text.length);
        for (String token : tokenizer) {
            System.out.println(token);
        }
    }

    private static void usingLemmatizationExamples() {
//        usingTheStanfordLemmatizer();
//        usingTheOpenNLPLemmatizer();
    }

    private static void usingTheStanfordLemmatizer() {
        System.out.println("Using Stanford Lemmatizer");
        String paragraph = "Similar to stemming is Lemmatization. "
                + "This is the process of finding its lemma, its form "
                + "as found in a dictionary.";

        StanfordCoreNLP pipeline;
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(paragraph);
        pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List<String> lemmas = new LinkedList<>();
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel word : sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(word.get(LemmaAnnotation.class));
            }
        }

        System.out.print("[");
        for (String element : lemmas) {
            System.out.print(element + " ");
        }
        System.out.println("]");
    }

    private static void usingTheOpenNLPLemmatizer() {
        // dictionary files downloaded from: https://code.google.com/p/xssm/downloads/detail?name=SimilarityUtils.zip&can=2&q=

        System.out.println("Starting the OpenNLP Lemmatizer");
        try {
            JWNLDictionary dictionary = new JWNLDictionary(
                    "C:\\Downloads\\xssm\\SimilarityUtils\\WordNet-2.0\\dict\\");
            paragraph = "Eat, drink, and be merry, for life is but a dream";
            String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(paragraph);
            for (String token : tokens) {
                String[] lemmas = dictionary.getLemmas(token, "");
                for (String lemma : lemmas) {
                    System.out.println("Token: " + token + "  Lemma: " + lemma);
                }
            }
        } catch (IOException | JWNLException ex) {
            Logger.getLogger(Chapter2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void usingStopwordsExamples() {
//        usingStopWordsClassExample();
//        usingLingpipeStopWord();
    }

    private static void usingStopWordsClassExample() {
        StopWords stopWords = new StopWords("stopwords.txt");
        SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
        paragraph = "A simple approach is to create a class "
                + "to hold and remove stopwords.";
        String tokens[] = simpleTokenizer.tokenize(paragraph);
        String list[] = stopWords.removeStopWords(tokens);
        for (String word : list) {
            System.out.println(word);
        }
        stopWords.displayStopWords();
    }

    private static void usingLingpipeStopWord() {
        paragraph = "A simple approach is to create a class "
                + "to hold and remove stopwords.";
        TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;
//        factory = new LowerCaseTokenizerFactory(factory);
        factory = new EnglishStopTokenizerFactory(factory);
//         factory = new PorterStemmerTokenizerFactory(factory);
        System.out.println(factory.tokenizer(paragraph.toCharArray(), 0, paragraph.length()));
        com.aliasi.tokenizer.Tokenizer tokenizer
                = factory.tokenizer(paragraph.toCharArray(), 0, paragraph.length());
        for (String token : tokenizer) {
            System.out.println(token);
        }
    }

    private static void normalizationProcessExamples() {
        usingLingPipeForNormalization();
        String text = "A Sample string with acronyms, IBM, and UPPER "
                + "and lower case letters.";
        String result = text.toLowerCase();
        System.out.println(result);
    }

    private static void usingLingPipeForNormalization() {
        paragraph = "A simple approach is to create a class "
                + "to hold and remove stopwords.";
        TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;
        factory = new LowerCaseTokenizerFactory(factory);
        factory = new EnglishStopTokenizerFactory(factory);
        factory = new PorterStemmerTokenizerFactory(factory);
        System.out.println(factory.tokenizer(paragraph.toCharArray(), 0, paragraph.length()));
        com.aliasi.tokenizer.Tokenizer tokenizer
                = factory.tokenizer(paragraph.toCharArray(), 0, paragraph.length());
        for (String token : tokenizer) {
            System.out.println(token);
        }
    }

    private static void trainingATokenizer() {
        createOpenNLPModel();
        try {
            paragraph = "A demonstration of how to train a tokenizer.";
            InputStream modelInputStream = new FileInputStream(new File(
                    ".", "mymodel.bin"));
            TokenizerModel model = new TokenizerModel(modelInputStream);
            Tokenizer tokenizer = new TokenizerME(model);
            String tokens[] = tokenizer.tokenize(paragraph);
            for (String token : tokens) {
                System.out.println(token);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void createOpenNLPModel() {
        try {
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    new FileInputStream("training-data.train"), "UTF-8");
            ObjectStream<TokenSample> sampleStream
                    = new TokenSampleStream(lineStream);
            TokenizerModel model = TokenizerME.train(
                    "en", sampleStream, true, 5, 100);
            BufferedOutputStream modelOutputStream = new BufferedOutputStream(
                    new FileOutputStream(new File("mymodel.bin")));
            model.serialize(modelOutputStream);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
