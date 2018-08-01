package packt;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.POIXMLProperties.CustomProperties;
import org.apache.poi.POIXMLProperties.ExtendedProperties;
import org.apache.poi.POIXMLPropertiesTextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Chapter8 {

    public static void main(String[] args) {
        extractingText();
        searches();
        usingStanfordPipeline();
        usingStanfordPipelineParallel();
    }

    private static void usingStanfordPipeline() {
        String text = "The robber took the cash and ran.";
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
//        String path = "C:\\Current Books\\NLP and Java\\Downloads\\stanford-ner-2014-10-26\\classifiers";
//        props.put("ner.model",path+"/english.muc.7class.distsim.crf.ser.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(text);
        System.out.println("Before annotate method executed ");
        Set<Class<?>> annotationSet = annotation.keySet();
        for (Class c : annotationSet) {
            System.out.println("\tClass: " + c.getCanonicalName());
        }
        pipeline.annotate(annotation);
        System.out.println("After annotate method executed ");
        annotationSet = annotation.keySet();
        for (Class c : annotationSet) {
            System.out.println("\tClass: " + c.getCanonicalName());
        }

        System.out.println("Total time: " + pipeline.timingInformation());
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                System.out.println("text of the token: " + word);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println("POS Tag: " + pos);
                String ne = token.get(NamedEntityTagAnnotation.class);
                System.out.println("ne: " + ne);
                Map<Integer, CorefChain> graph
                        = token.get(CorefChainAnnotation.class);
                System.out.println("graph: " + graph);
            }
            Tree tree = sentence.get(TreeAnnotation.class);
            System.out.println("tree: " + tree);

            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            System.out.println("dependencies: " + dependencies);

            Map<Integer, CorefChain> graph
                    = annotation.get(CorefChainAnnotation.class);
            System.out.println("graph: " + graph);
        }
    }

    private static void usingStanfordPipelineParallel() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        String path = "C:\\Current Books\\NLP and Java\\Downloads\\stanford-ner-2014-10-26\\classifiers";
        props.put("ner.model", path + "/english.muc.7class.distsim.crf.ser.gz");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation1 = new Annotation("The robber took the cash and ran.");
        Annotation annotation2 = new Annotation("The policeman chased him down the street.");
        Annotation annotation3 = new Annotation("A passerby, watching the action, tripped the thief as he passed by.");
        Annotation annotation4 = new Annotation("They all lived happily everafter, except for the thief of course.");
        ArrayList<Annotation> list = new ArrayList();
        list.add(annotation1);
        list.add(annotation2);
        list.add(annotation3);
        list.add(annotation4);
        Iterable<Annotation> iterable = list;

        pipeline.annotate(iterable);

        System.out.println("Total time: " + pipeline.timingInformation());
        List<CoreMap> sentences = annotation2.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println("Word: " + word + " POS Tag: " + pos);
            }
        }
    }

    private static void searches() {
        try (InputStream is = new FileInputStream(
                new File("C:/Current Books/NLP and Java/Models/en-sent.bin"));
                FileReader fr = new FileReader("Twenty Thousands.txt");
                BufferedReader br = new BufferedReader(fr)) {
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + " ");
            }
            String sentences[] = detector.sentDetect(sb.toString());
            System.out.println(sentences.length);
            // Convert each character to lowercase
            for (int i = 0; i < sentences.length; i++) {
                sentences[i] = sentences[i].toLowerCase();
            }

            // Remove stopwords
            StopWords stopWords = new StopWords("stop-words_english_2_en.txt");
            for (int i = 0; i < sentences.length; i++) {
                sentences[i] = stopWords.removeStopWords(sentences[i]);
            }

            // Create map
            HashMap<String, Word> wordMap = new HashMap();
            for (int sentenceIndex = 0; sentenceIndex < sentences.length; sentenceIndex++) {
                String words[] = WhitespaceTokenizer.INSTANCE.tokenize(sentences[sentenceIndex]);
                Word word;
                for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                    String newWord = words[wordIndex];
                    if (wordMap.containsKey(newWord)) {
                        word = wordMap.remove(newWord);
                    } else {
                        word = new Word();
                    }
                    word.addWord(newWord, sentenceIndex, wordIndex);
                    wordMap.put(newWord, word);
                }
            }
            System.out.println(wordMap.size());

            // Locate word in document
            Word word = wordMap.get("reef");
            ArrayList<Positions> positions = word.getPositions();
            for (Positions position : positions) {
                System.out.println(word.getWord() + " is found at line " 
                        + position.sentence + ", word " + position.position);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void extractingText() {
        usingBoilerpipe();
        usingPOI();
        usingPDFBox();
    }

    private static void usingBoilerpipe() {
        try {
            URL url = new URL("http://en.wikipedia.org/wiki/Berlin");
            HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
            InputSource is = htmlDoc.toInputSource();
            TextDocument document
                    = new BoilerpipeSAXInput(is).getTextDocument();

            System.out.println(document.getText(true, true));

            System.out.println("--------------------------------");
            List<TextBlock> blocks = document.getTextBlocks();
            for (TextBlock block : blocks) {
                System.out.println(block.isContent());
                System.out.println(block.getText());
                System.out.println(block.getNumWords());
                System.out.println("------");
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (BoilerpipeProcessingException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void usingPDFBox() {
        try {
            File file = new File("TestDocument.pdf");
            PDDocument pdDocument = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);
            System.out.println(text);
            pdDocument.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void usingPOI() {
        try {
            FileInputStream fis = new FileInputStream("TestDocument.docx");
            POITextExtractor textExtractor = ExtractorFactory.createExtractor(fis);
            System.out.println(textExtractor.getText());
            
            POITextExtractor metaExtractor = textExtractor.getMetadataTextExtractor();
            System.out.println(metaExtractor.getText());
            System.out.println();
            
            fis = new FileInputStream("TestDocument.docx");
            POIXMLPropertiesTextExtractor properties = new POIXMLPropertiesTextExtractor(new XWPFDocument(fis));
            System.out.println(properties.getText());
            System.out.println();
            
            CoreProperties coreProperties = properties.getCoreProperties();
            System.out.println("Core Properties");
            System.out.println(properties.getCorePropertiesText());
            
            System.out.println();
            System.out.println("Creator: " + coreProperties.getCreator());
            System.out.println("Date Created: " + coreProperties.getCreated());
            System.out.println("Date Last Modified: " + coreProperties.getModified());
            
            System.out.println();
            System.out.println("Extended Properties");
            ExtendedProperties extendedProperties = properties.getExtendedProperties();
            System.out.println(properties.getExtendedPropertiesText());
            System.out.println();
            System.out.println("Application: " + extendedProperties.getApplication());
            System.out.println("Application Version: " + extendedProperties.getAppVersion());
            System.out.println("Pages: " + extendedProperties.getPages());
            
            System.out.println();    
            System.out.println("Custom Properties: " );
            System.out.println(properties.getCustomPropertiesText());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OpenXML4JException | XmlException ex) {
            ex.printStackTrace();
        }
    }
}
