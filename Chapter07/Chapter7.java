package packt;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Chapter7 {

    public static void main(String[] args) {
        usingOpenNLP();
//        usingStanford();
    }

    static Set<String> nounPhrases = new HashSet<>();

    private static void usingOpenNLP() {
        String fileLocation = getModelDir() + "/en-parser-chunking.bin";
        System.out.println(fileLocation);
        try (InputStream modelInputStream = new FileInputStream(fileLocation);) {
            ParserModel model = new ParserModel(modelInputStream);
            Parser parser = ParserFactory.create(model);
            String sentence = "The cow jumped over the moon";
            // Used to demonstrate difference between NER and Parser
            sentence = "He was the last person to see Fred.";

            Parse parses[] = ParserTool.parseLine(sentence, parser, 3);
            for (Parse parse : parses) {
                // First display
                parse.show();
                // Second display
//                parse.showCodeTree();
                // Third display
//                System.out.println("Children");
//                Parse children[] = parse.getChildren();
//                for (Parse parseElement : children) {
//                    System.out.println(parseElement);
//                    System.out.println(parseElement.getText());
//                    System.out.println(parseElement.getType());
//                    Parse tags[] = parseElement.getTagNodes();
//                    System.out.println("Tags");
//                    for (Parse tag : tags) {
//                        System.out.println("[" + tag + "]" + " type: " + tag.getType()
//                                + "  Probability: " + tag.getProb()
//                                + "  Label: " + tag.getLabel());
//                    }
//                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getModelDir() {
        return "C:/Current Books/NLP and Java/Models";
    }

    private static void usingStanfordParsers() {
        usingStanford();
//        usingStanfordLexicalizedParser();
    }

    private static void usingStanford() {
        usingStanfordLexicalizedParser();
//        usingStanfordRelationExtraction();
//        usingStanfordCoreferenceResolution();
//        extractingRelations();
    }

    private static void usingStanfordLexicalizedParser() {
        String parserModel = "C:/Current Books in Progress/NLP and Java/Models/edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(parserModel);

        // This option shows parsing a list of correctly tokenized words
        System.out.println("---First option");
        String[] senetenceArray = {"The", "cow", "jumped", "over", "the", "moon", "."};
        List<CoreLabel> words = Sentence.toCoreLabelList(senetenceArray);

        Tree parseTree = lexicalizedParser.apply(words);
        parseTree.pennPrint();
        System.out.println();

        // This option shows loading and using an explicit tokenizer
        System.out.println("---Second option");
        String sentence = "The cow jumped over the moon.";
        TokenizerFactory<CoreLabel> tokenizerFactory
                = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tokenizer
                = tokenizerFactory.getTokenizer(new StringReader(sentence));
        List<CoreLabel> wordList = tokenizer.tokenize();
        parseTree = lexicalizedParser.apply(wordList);

        TreebankLanguagePack tlp = lexicalizedParser.treebankLanguagePack(); // PennTreebankLanguagePack for English
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        for (TypedDependency dependency : tdl) {
            System.out.println("Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                    + "] Dependent Word: [" + dependency.dep() + "]");
        }
        System.out.println();

        // You can also use a TreePrint object to print trees and dependencies
//        System.out.println("---Using TreePrint");
//        TreePrint treePrint = new TreePrint("penn,typedDependenciesCollapsed");
//        treePrint.printTree(parseTree);
//        System.out.println("TreePrint Formats");
//        for (String format : TreePrint.outputTreeFormats) {
//            System.out.println(format);
//        }
//        System.out.println();
    }

    private static void usingStanfordCoreferenceResolution() {
        System.out.println("StanfordCoreferenceResolution");
        String sentence = "He took his cash and she took her change " 
                + "and together they bought their lunch.";
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        System.out.println("Sentence: " + sentence);
        Map<Integer, CorefChain> corefChainMap = annotation.get(CorefChainAnnotation.class);

        Set<Integer> set = corefChainMap.keySet();
        Iterator<Integer> setIterator = set.iterator();
        while(setIterator.hasNext()) {
            CorefChain corefChain = corefChainMap.get(setIterator.next());
            System.out.println("CorefChain: " + corefChain);
            System.out.print("ClusterId: " + corefChain.getChainID());
            CorefMention mention = corefChain.getRepresentativeMention();
            System.out.println(" CorefMention: " + mention + " Span: [" + mention.mentionSpan + "]");

            List<CorefMention> mentionList = corefChain.getMentionsInTextualOrder();
            Iterator<CorefMention> mentionIterator = mentionList.iterator();
            while(mentionIterator.hasNext()) {
                CorefMention cfm = mentionIterator.next();
                System.out.println("\tMention: " + cfm + " Span: [" + mention.mentionSpan + "]");
                System.out.print("\tMention Type: " + cfm.mentionType + " Gender: " + cfm.gender);
                System.out.println(" Start: " + cfm.startIndex + " End: " + cfm.endIndex);
            }
            System.out.println();
        }
    }

    private static void extractingRelations() {
        String question = "Who is the 32rd president of the United States?";
//        question = "Who was the 32rd president of the United States?";
//        question = "The 32rd president of the United States was who?";
//        question = "The 32rd president is who of the United States?";
//        question = "What was the 3rd President's party?";
//        question = "When was the 12th president inaugurated";
//        question = "Where is the 30th president's home town?";

        String parserModel = "C:/Current Books/NLP and Java/Models/edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(parserModel);

        TokenizerFactory<CoreLabel> tokenizerFactory
                = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tokenizer
                = tokenizerFactory.getTokenizer(new StringReader(question));
        List<CoreLabel> wordList = tokenizer.tokenize();
        Tree parseTree = lexicalizedParser.apply(wordList);

        TreebankLanguagePack tlp = lexicalizedParser.treebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        for (TypedDependency dependency : tdl) {
            System.out.println("Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                    + "] Dependent Word: [" + dependency.dep() + "]");
        }

        System.out.println();
        System.out.println("You asked: " + question);
        for (TypedDependency dependency : tdl) {
            if ("nominal subject".equals(dependency.reln().getLongName())
                    && "who".equalsIgnoreCase(dependency.gov().originalText())) {
                System.out.println("Found Who question --- Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                        + "] Dependent Word: [" + dependency.dep() + "]");
                processWhoQuestion(tdl);
            } else if ("nominal subject".equals(dependency.reln().getLongName())
                    && "what".equalsIgnoreCase(dependency.gov().originalText())) {
                System.out.println("Found What question --- Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                        + "] Dependent Word: [" + dependency.dep() + "]");
            } else if ("adverbial modifier".equals(dependency.reln().getLongName())
                    && "when".equalsIgnoreCase(dependency.dep().originalText())) {
                System.out.println("Found When question --- Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                        + "] Dependent Word: [" + dependency.dep() + "]");
            } else if ("adverbial modifier".equals(dependency.reln().getLongName())
                    && "where".equalsIgnoreCase(dependency.dep().originalText())) {
                System.out.println("Found Where question --- Governor Word: [" + dependency.gov() + "] Relation: [" + dependency.reln().getLongName()
                        + "] Dependent Word: [" + dependency.dep() + "]");
            }
        }
    }

    private static void processWhoQuestion(List<TypedDependency> tdl) {
        System.out.println("Processing Who Question");
        List<President> list = createPresidentList();
        for (TypedDependency dependency : tdl) {
            if ("president".equalsIgnoreCase(dependency.gov().originalText())
                    && "adjectival modifier".equals(dependency.reln().getLongName())) {
                String positionText = dependency.dep().originalText();
                int position = getOrder(positionText) - 1;
                System.out.println("The president is " + list.get(position).getName());
            }
        }
    }

    private static int getOrder(String position) {
        String tmp = "";
        int i = 0;
        while (Character.isDigit(position.charAt(i))) {
            tmp += position.charAt(i++);
        }
        return Integer.parseInt(tmp);
    }

    private static List<President> createPresidentList() {
        ArrayList<President> list = new ArrayList<>();
        String line = null;
        try (FileReader reader = new FileReader("PresidentList");
                BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
                String tokens[] = simpleTokenizer.tokenize(line);
                String name = "";
                String start = "";
                String end = "";
                int i = 0;
                while (!"(".equals(tokens[i])) {
                    name += tokens[i] + " ";
                    i++;
                }
                start = tokens[i + 1];
                end = tokens[i + 3];
                if (end.equalsIgnoreCase("present")) {
                    end = start;
                }
                list.add(new President(name, Integer.parseInt(start),
                        Integer.parseInt(end)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
