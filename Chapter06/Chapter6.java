package packt;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.Files;
import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class Chapter6 {

    private static String inputText = null;
    private static String toto = "Toto belongs to Dorothy Gale, the heroine of "
            + "the first and many subsequent books. In the first "
            + "book, he never spoke, although other animals, native "
            + "to Oz, did. In subsequent books, other animals "
            + "gained the ability to speak upon reaching Oz or "
            + "similar lands, but Toto remained speechless.";
    private static String garfield = "Garfield is a comic strip created by Jim "
            + "Davis. Published since June 19, 1978, it chronicles "
            + "the life of the title character, the cat Garfield "
            + "(named after the grandfather of Davis); his owner, "
            + "Jon Arbuckle; and Jon's dog, Odie.";

    private static String calico = "This cat is also known as a calimanco cat or "
            + "clouded tiger cat, and by the abbreviation 'tortie'. "
            + "In the cat fancy, a tortoiseshell cat is patched "
            + "over with red (or its dilute form, cream) and black "
            + "(or its dilute blue) mottled throughout the coat.";

    public static void main(String[] args) {
        inputText = toto;
//        inputText = garfield;
//        inputText = calico;
//        trainingOpenNLPModel();
//        usingOpenNLP();
//        usingStandfordClassifier();
//        usingStanfordSentimentAnalysis();
        usingLingPipe();
    }

    private static void trainingOpenNLPModel() {
        DoccatModel model = null;
        try (InputStream dataIn = new FileInputStream("en-animal.train");
                OutputStream dataOut = new FileOutputStream("en-animal.model");) {
            ObjectStream<String> lineStream
                    = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            model = DocumentCategorizerME.train("en", sampleStream);

            // Save the model
            OutputStream modelOut = null;
            modelOut = new BufferedOutputStream(dataOut);
            model.serialize(modelOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void usingOpenNLP() {
        try (InputStream modelIn = new FileInputStream(
                new File("en-animal.model"));) {
            DoccatModel model = new DoccatModel(modelIn);
            DocumentCategorizerME categorizer = new DocumentCategorizerME(model);
            double[] outcomes = categorizer.categorize(inputText);
            for (int i = 0; i < categorizer.getNumberOfCategories(); i++) {
                String category = categorizer.getCategory(i);
                System.out.println(category + " - " + outcomes[i]);
            }
            System.out.println(categorizer.getBestCategory(outcomes));
            System.out.println(categorizer.getAllResults(outcomes));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static File getModelDir() {
        return new File("C:\\Current Books in Progress\\NLP and Java\\Models");
    }

    private static void usingStandfordClassifier() {
//        String dir = "C:/Current Books in Progress/NLP and Java/Downloads/Stanford/stanford-classifier-2014-10-26/";
        ColumnDataClassifier cdc = new ColumnDataClassifier("box.prop");
        Classifier<String, String> classifier
                = cdc.makeClassifier(cdc.readTrainingExamples("box.train"));
        for (String line : ObjectBank.getLineIterator("box.test", "utf-8")) {
            // instead of the method in the line below, if you have the individual elements
            // already you can use cdc.makeDatumFromStrings(String[])
            Datum<String, String> datum = cdc.makeDatumFromLine(line);
            System.out.println("Datum: {" + line + "]\tPredicted Category: " + classifier.classOf(datum));
//            System.out.println(" Scores: " + classifier.scoresOf(datum));
//            Counter<String> counter = classifier.scoresOf(datum);
//            Set<String> set = counter.keySet();
//            for (String element : set) {
//                System.out.printf("Scores - %-6s: %5.2f ", element, counter.getCount(element));
//            }
//            System.out.println();
        }

 
        System.out.println();
        String sample[] = {"", "6.90", "9.8", "15.69"};
        Datum<String, String> datum = cdc.makeDatumFromStrings(sample);
        System.out.println("Category: " + classifier.classOf(datum));
    }

    private static void usingStanfordSentimentAnalysis() {
        String review = "An overly sentimental film with a somewhat "
                + "problematic message, but its sweetness and charm "
                + "are occasionally enough to approximate true depth "
                + "and grace. ";

        String sam = "Sam was an odd sort of fellow. Not prone to angry and "
                + "not prone to merriment. Overall, an odd fellow.";
        String mary = "Mary thought that custard pie was the best pie in the "
                + "world. However, she loathed chocolate pie.";
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(review);
        pipeline.annotate(annotation);

        System.out.println("---sentimentText");
        String[] sentimentText = {"Very Negative", "Negative", "Neutral",
            "Positive", "Very Positive"};
        for (CoreMap sentence : annotation.get(
                CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(
                    SentimentCoreAnnotations.AnnotatedTree.class);
            System.out.println("---Number of children: " + tree.numChildren());
            System.out.println("[" + tree.getChild(0) + "][" + tree.getChild(1) + "]");
            tree.printLocalTree();
            int score = RNNCoreAnnotations.getPredictedClass(tree);
            System.out.println(sentimentText[score]);
        }

        // Classifer
        CRFClassifier crf
                = CRFClassifier.getClassifierNoExceptions(
                        "C:/Current Books in Progress/NLP and Java/Models"
                        + "/english.all.3class.distsim.crf.ser.gz");
        String S1 = "Good afternoon Rajat Raina, how are you today?";
        String S2 = "I go to school at Stanford University, which is located in California.";
        System.out.println(crf.classifyToString(S1));
        System.out.println(crf.classifyWithInlineXML(S2));
        System.out.println(crf.classifyToString(S2, "xml", true));

        Object classification[] = crf.classify(S2).toArray();
        for (int i = 0; i < classification.length; i++) {
            System.out.println(classification[i]);
        }
    }

    //----------------------------------------------------------------------------
    private static String[] categories
            = {"soc.religion.christian",
                "talk.religion.misc",
                "alt.atheism",
                "misc.forsale"};

    private static JointClassifier<CharSequence> compiledClassifier;

    private static void usingLingPipe() {
//        trainingLingPipeClassificationModels();
//        usingLingPipeModels();
        usingLingPipeSentimentAnalysis();
//        classifyLingPipeSLanguageAnalysis();
    }

    private static int nGramSize = 6;
    private static DynamicLMClassifier<NGramProcessLM> classifier
            = DynamicLMClassifier.createNGramProcess(categories, nGramSize);

    private static void trainingLingPipeClassificationModels() {
        final String directory = "C:/Current Books/NLP and Java/Downloads/lingpipe-4.1.0/demos";
        final File trainingDirectory
                = new File(directory + "/data/fourNewsGroups/4news-train");

        for (int i = 0; i < categories.length; ++i) {
            final File classDir = new File(trainingDirectory, categories[i]);

            String[] trainingFiles = classDir.list();
            for (int j = 0; j < trainingFiles.length; ++j) {
                try {
                    File file = new File(classDir, trainingFiles[j]);
                    String text = Files.readFromFile(file, "ISO-8859-1");
                    Classification classification
                            = new Classification(categories[i]);
                    Classified<CharSequence> classified
                            = new Classified<>((CharSequence)text, classification);
                    classifier.handle(classified);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        try {
            AbstractExternalizable.compileTo(
                    (Compilable) classifier,
                    new File("classifier.model"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void usingLingPipeModels() {
        String text = "Finding a home for sale has never been "
                + "easier. With Homes.com, you can search new "
                + "homes, foreclosures, multi-family homes, "
                + "as well as condos and townhouses for sale. "
                + "You can even search our real estate agent "
                + "directory to work with a professional "
                + "Realtor and find your perfect home.";
//        text = "Luther taught that salvation and subsequently "
//                + "eternity in heaven is not earned by good deeds "
//                + "but is received only as a free gift of God's "
//                + "grace through faith in Jesus Christ as redeemer "
//                + "from sin and subsequently eternity in Hell.";
        LMClassifier classifier = null;
        try {
            //        text =
//                "Homeowners may employ the services of marketing or online listing companies or market their own property but do not pay a commission and represent themselves with the help of a lawyer or Solicitor (mostly in Commonwealth) throughout the sale.";
            classifier = (LMClassifier) AbstractExternalizable.readObject(new File("classifier.model"));
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        JointClassification classification
                = classifier.classify(text);

        System.out.println("---------------");
        System.out.println("Text: " + text);
        String bestCategory = classification.bestCategory();
        System.out.println("Best Category: " + bestCategory);
        for (int i = 0; i < categories.length; i++) {
            double score = classification.score(i);
            double probability = classification.jointLog2Probability(i);
            String category = classification.category(i);
            System.out.printf(" %3d  - Category: %-24s  Score: %6.2f  jointLog2Probability: %6.2f%n",
                    i, category, score, probability);
        }

//            }
//        }        
    }

    private static void usingLingPipeSentimentAnalysis() {
        categories = new String[2];
        categories[0] = "neg";
        categories[1] = "pos";
        nGramSize = 8;
        classifier = DynamicLMClassifier.createNGramProcess(categories, nGramSize);

        trainingLingPipeSentimentAnalysis();
        classifyLingPipeSentimentAnalysis();
    }

    private static void trainingLingPipeSentimentAnalysis() {
        String directory = "C:/Current Books/NLP and Java/Downloads/Sentiment Data";
        File trainingDirectory = new File(directory, "txt_sentoken");
        System.out.println("\nTraining.");
        for (int i = 0; i < categories.length; ++i) {
            Classification classification
                    = new Classification(categories[i]);
            File file = new File(trainingDirectory, categories[i]);
            File[] trainingFiles = file.listFiles();
            for (int j = 0; j < trainingFiles.length; ++j) {
                try {
                    String review = Files.readFromFile(trainingFiles[j], "ISO-8859-1");
                    Classified<CharSequence> classified;
                    classified = new Classified<>((CharSequence)review, classification);
                    classifier.handle(classified);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void classifyLingPipeSentimentAnalysis() {
        System.out.println("---------------");
        //http://www.rottentomatoes.com/m/forrest_gump/
        String review = "An overly sentimental film with a somewhat "
                + "problematic message, but its sweetness and charm "
                + "are occasionally enough to approximate true depth "
                + "and grace. ";
        System.out.println("Text: " + review);
        Classification classification
                = classifier.classify(review);
        String bestCategory = classification.bestCategory();
        System.out.println("Best Category: " + bestCategory);

        for (String category : classifier.categories()) {
            System.out.println(category);
        }
    }

    private static void classifyLingPipeSLanguageAnalysis() {
        System.out.println("---------------");
        //http://www.rottentomatoes.com/m/forrest_gump/
        String text = "An overly sentimental film with a somewhat "
                + "problematic message, but its sweetness and charm "
                + "are occasionally enough to approximate true depth "
                + "and grace. ";
        text = "Svenska är ett östnordiskt språk som talas av cirka "
                + "tio miljoner personer[1], främst i Finland "
                + "och Sverige.";
//        text = "¡Buenos días, clase! Good morning, class! Hola, ¿Cómo están hoy? Hello, how are you today? Adiós, ¡hasta luego! Bye, see you soon!";
        System.out.println("Text: " + text);
        LMClassifier classifier = null;
        try {
            classifier = (LMClassifier) AbstractExternalizable.readObject(
                    new File("C:/Current Books/NLP and Java/Models/langid-leipzig.classifier"));
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        Classification classification
                = classifier.classify(text);
        String bestCategory = classification.bestCategory();
        System.out.println("Best Language: " + bestCategory);

        for (String category : classifier.categories()) {
            System.out.println(category);
        }
    }
}
