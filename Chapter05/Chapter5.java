package packt;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tag.TagLattice;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.MutableTagDictionary;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSEvaluator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class Chapter5 {

    private static String sentences[] = {
        "The voyage of the Abraham Lincoln was for a long time marked by no special incident.",
        "But one circumstance happened which showed the wonderful dexterity of Ned Land, and proved what confidence we might place in him.",
        "The 30th of June, the frigate spoke some American whalers, from whom we learned that they knew nothing about the narwhal.",
        "But one of them, the captain of the Monroe, knowing that Ned Land had shipped on board the Abraham Lincoln, begged for his help in chasing a whale they had in sight."};
    private static String theSentence
            = "The voyage of the Abraham Lincoln was for a long time marked by "
            + "no special incident.";
    private static String[] sentence = {"The", "voyage", "of", "the",
        "Abraham", "Lincoln", "was", "for", "a", "long", "time", "marked",
        "by", "no", "special", "incident."};

    public static void main(String[] args) {
//        usingTokenizeSentence();
//        usingOpenNLPExamples();
//        usingStanfordAPI();
        usingLingPipe();
//        trainingAnOpenNLPPOSModel();
    }

    private static void usingOpenNLPExamples() {
        usingOpenNLPPOSModel();
//        usingOpenNLPChunker();
//        usingThePOSDictionary();
    }

    private static void usingTokenizeSentence() {
        String words[] = tokenizeSentence(theSentence);
        for (String word : words) {
            System.out.print(word + " ");
        }
        System.out.println();
    }

    private static String[] tokenizeSentence(String sentence) {
        //First techniquue
        String words[] = sentence.split("S+");

        // Second technique
        words = WhitespaceTokenizer.INSTANCE.tokenize(sentence);

        return words;
    }

    private static void usingOpenNLPPOSModel() {
        System.out.println("OpenNLP POSModel Examples");
        try (InputStream modelIn = new FileInputStream(
                new File(getModelDir(), "en-pos-maxent.bin"));) {
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);

            // Introduction sentences
//            sentence = tokenizeSentence("The cow jumped over the moon.");
//            sentence = tokenizeSentence("Bill used the force to force the manger to tear the bill in two.");
//            sentence = tokenizeSentence("AFAIK she H8 cth!");
//            sentence = tokenizeSentence("BTW had a GR8 tym at the party BBIAM.");
//            sentence = tokenizeSentence("Whether \"Blue\" was correct or not (it’s not) is debatable");
            String tags[] = tagger.tag(sentence);
            double probs[] = tagger.probs();

            for (int i = 0; i < sentence.length; i++) {
                System.out.print(sentence[i] + "/" + tags[i] + " ");
            }
            System.out.println();
            // Use import opennlp.tools.util.Sequence; instead of
            // import opennlp.model.Sequence
            System.out.println("topSequences");
            Sequence topSequences[] = tagger.topKSequences(sentence);
            for (int i = 0; i < topSequences.length; i++) {
                System.out.println(topSequences[i]);
//                List<String> list = topSequences[i].getOutcomes();
//                for(String outcome : list) {
//                    System.out.print(outcome + " ");
//                    System.out.println();
//                }
            }
            System.out.println();

            System.out.println("occurrences and probabilities");
//            DecimalFormat decimalFormat = new DecimalFormat("##.###");
            for (int i = 0; i < topSequences.length; i++) {
                List<String> outcomes = topSequences[i].getOutcomes();
                double probabilities[] = topSequences[i].getProbs();
                for (int j = 0; j < outcomes.size(); j++) {
                    System.out.printf("%s/%5.3f ",outcomes.get(j),probabilities[j]);
                }
                System.out.println();
            }
            System.out.println();
//            
//            // Getting the dictionasry tags
//            POSTaggerFactory ptf = model.getFactory();
//            TagDictionary tagDictionary = ptf.getTagDictionary();
//            String dictionaryTags[] = tagDictionary.getTags("the");
//            System.out.println(dictionaryTags.length);
//            for(String word : dictionaryTags) {
//                 System.out.println(word);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void usingOpenNLPChunker() {
        try (
                InputStream posModelStream = new FileInputStream(
                        getModelDir() + "\\en-pos-maxent.bin");
                InputStream chunkerStream = new FileInputStream(
                        getModelDir() + "\\en-chunker.bin");) {
                    POSModel model = new POSModel(posModelStream);
                    POSTaggerME tagger = new POSTaggerME(model);
                    
                    // Used to create sample data for trainer
//                    for (String sentence : sentences) {
//                        String sen[] = tokenizeSentence(sentence);
//                        String tags[] = tagger.tag(sen);
//                        for (int i = 0; i < tags.length; i++) {
////                    for (String token : sentence) {
//                            System.out.print(sen[i] + "/" + tags[i] + " ");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println();

                    String tags[] = tagger.tag(sentence);
                    for (int i = 0; i < tags.length; i++) {
//                    for (String token : sentence) {
                        System.out.print(sentence[i] + "/" + tags[i] + " ");
                    }
                    System.out.println();

                    // chunker
                    System.out.println("------------Chunker -----------");
                    ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
                    ChunkerME chunkerME = new ChunkerME(chunkerModel);
                    String result[] = chunkerME.chunk(sentence, tags);

                    for (int i = 0; i < result.length; i++) {
                        System.out.println("[" + sentence[i] + "] " + result[i]);
                    }

                    System.out.println("------------Chunker Spans -----------");
                    Span[] spans = chunkerME.chunkAsSpans(sentence, tags);
                    for (Span span : spans) {
                        System.out.print("Type: " + span.getType() + " - " + " Begin: "
                                + span.getStart() + " End:" + span.getEnd()
                                + " Length: " + span.length() + "  [");
                        for (int j = span.getStart(); j < span.getEnd(); j++) {
                            System.out.print(sentence[j] + " ");
                        }
                        System.out.println("]");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

    }

    private static void usingThePOSDictionary() {
        try (InputStream modelIn = new FileInputStream(
                new File(getModelDir(), "en-pos-maxent.bin"));) {
            POSModel model = new POSModel(modelIn);
            POSTaggerFactory posTaggerFactory = model.getFactory();
            MutableTagDictionary tagDictionary = (MutableTagDictionary) posTaggerFactory.getTagDictionary();

            String tags[] = tagDictionary.getTags("force");
            for (String tag : tags) {
                System.out.print("/" + tag);
            }
            System.out.println();

            System.out.println("Old Tags");
            // Add old tags first
            String newTags[] = new String[tags.length + 1];
            for (int i = 0; i < tags.length; i++) {
                newTags[i] = tags[i];
            }
            newTags[tags.length] = "newTag";
//            String oldTags[] = tagDictionary.put("force", "newTag");
            String oldTags[] = tagDictionary.put("force", newTags);
            // Display old tags
            for (String tag : oldTags) {
                System.out.print("/" + tag);
            }
            System.out.println();
            System.out.println("New Tags");
            // Display new tags
            tags = tagDictionary.getTags("force");
            for (String tag : tags) {
                System.out.print("/" + tag);
            }
            System.out.println();

//            Creating a new Factory with dictionary
            System.out.println();
            POSTaggerFactory newFactory = new POSTaggerFactory();
            newFactory.setTagDictionary(tagDictionary);
            tags = newFactory.getTagDictionary().getTags("force");
            for (String tag : tags) {
                System.out.print("/" + tag);
            }
            System.out.println();
            tags = newFactory.getTagDictionary().getTags("bill");
            for (String tag : tags) {
                System.out.print("/" + tag);
            }
            System.out.println();
            createDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDictionary() {
        try (InputStream dictionaryIn = new FileInputStream(
                new File("dictionary.txt"));) {
            POSDictionary dictionary = POSDictionary.create(dictionaryIn);
            Iterator<String> iterator = dictionary.iterator();
            while (iterator.hasNext()) {
                String entry = iterator.next();
                String tags[] = dictionary.getTags(entry);
                System.out.print(entry + " ");
                for (String tag : tags) {
                    System.out.print("/" + tag);
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getModelDir() {
        return new File("C:\\Current Books in Progress\\NLP and Java\\Models");
    }

    private static void trainingAnOpenNLPPOSModel() {
        POSModel model = null;
        try (InputStream dataIn = new FileInputStream("sample.train");) {
            ObjectStream<String> lineStream
                    = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
            model = POSTaggerME.train("en", sampleStream,
                    TrainingParameters.defaultParams(), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStream modelOut = new BufferedOutputStream(
                new FileOutputStream(new File("en_pos_verne.bin")));) {
            model.serialize(modelOut);
        } catch (IOException e) {
            // Failed to save model
            e.printStackTrace();
        }

    }

    private static void usingStanfordAPI() {
        usingStanfordMaxentPOS();
//        usingStanfordPOSTagger();
    }

    private static void usingStanfordMaxentPOS() {
        try {
            MaxentTagger tagger = new MaxentTagger(getModelDir() + "//wsj-0-18-bidirectional-distsim.tagger");
//            MaxentTagger tagger = new MaxentTagger(getModelDir() + "//gate-EN-twitter.model");
//            System.out.println(tagger.tagString("AFAIK she H8 cth!"));
//            System.out.println(tagger.tagString("BTW had a GR8 tym at the party BBIAM."));
            List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("sentences.txt")));
            for (List<HasWord> sentence : sentences) {
                List<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
                // Simple display
                System.out.println("---" + taggedSentence);
                // Simple conversion to String
//                System.out.println(Sentence.listToString(taggedSentence, false));
                // Display of words and tags
//                for (TaggedWord taggedWord : taggedSentence) {
//                    System.out.print(taggedWord.word() + "/" + taggedWord.tag() + " ");
//                }
//                System.out.println();
                // List of specifc tags
//                System.out.print("NN Tagged: ");
//                for (TaggedWord taggedWord : taggedSentence) {
//                    if (taggedWord.tag().startsWith("NN")) {
//                        System.out.print(taggedWord.word() + " ");
//                    }
//                }
//                System.out.println();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void usingStanfordPOSTagger() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        props.put("pos.model", "C:\\Current Books in Progress\\NLP and Java\\Models\\english-caseless-left3words-distsim.tagger");
        props.put("pos.maxlen", 10);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(theSentence);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.print(word + "/" + pos + " ");
            }
            System.out.println();

            try {
                pipeline.xmlPrint(document, System.out);
                pipeline.prettyPrint(document, System.out);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void usingLingPipe() {
        usingLingPipeGeneralBrown();
    }

    private static void usingLingPipeGeneralBrown() {
        // pos-en-bio-genia
        // pos-en-general-brown
        try (
                FileInputStream inputStream = new FileInputStream(getModelDir()
                        + "//pos-en-general-brown.HiddenMarkovModel");
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);) {
            HiddenMarkovModel hmm = (HiddenMarkovModel) objectStream.readObject();
            HmmDecoder decoder = new HmmDecoder(hmm);

            TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
            char[] charArray = theSentence.toCharArray();
            Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(charArray, 0, charArray.length);
            String[] tokens = tokenizer.tokenize();

            List<String> tokenList = Arrays.asList(tokens);
            String[] sentence = {"Bill", "used", "the", "force",
                "to", "force", "the", "manager", "to", "tear", "the", "bill",
                "in", "two."};
            tokenList = Arrays.asList(sentence);
            Tagging<String> tagString = decoder.tag(tokenList);

            for (int i = 0; i < tagString.size(); ++i) {
                System.out.print(tagString.token(i) + "/" + tagString.tag(i) + " ");
            }
            System.out.println();

            // N-best results
            int maxResults = 5;

            Iterator<ScoredTagging<String>> iterator = decoder.tagNBest(tokenList, maxResults);
            while (iterator.hasNext()) {
                ScoredTagging<String> scoredTagging = iterator.next();
                System.out.printf("Score: %7.3f   Sequence: ", scoredTagging.score());
                for (int i = 0; i < tokenList.size(); ++i) {
                    System.out.print(scoredTagging.token(i) + "/" + scoredTagging.tag(i) + " ");
                }
                System.out.println();
            }
            // Confidence-Based Results
            TagLattice<String> lattice = decoder.tagMarginal(tokenList);
            for (int index = 0; index < tokenList.size(); index++) {
                ConditionalClassification classification = lattice.tokenClassification(index);
                System.out.printf("%-8s", tokenList.get(index));
                for (int i = 0; i < 4; ++i) {
                    double score = classification.score(i);
                    String tag = classification.category(i);
                    System.out.printf("%7.3f/%-3s ", score, tag);
                }
                System.out.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
