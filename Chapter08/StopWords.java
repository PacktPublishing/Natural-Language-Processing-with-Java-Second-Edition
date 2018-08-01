package packt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class StopWords {

    private String[] defaultStopWords = {"i", "a", "about", "an", "are", "as", "at",
        "be", "by", "com", "for", "from", "how", "in", "is", "it", "of", "on",
        "or", "that", "the", "this", "to", "was", "what", "when", "where",
        "who", "will", "with"};

    private static HashSet stopWords = new HashSet();

    public StopWords() {
        stopWords.addAll(Arrays.asList(defaultStopWords));
    }

    public StopWords(String fileName) {
        try {
            BufferedReader bufferedreader
                    = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = bufferedreader.readLine()) != null) {
//                line = bufferedreader.readLine();
                System.out.println("---Adding: [" + line + "]" + (int)line.charAt(0));
                stopWords.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addStopWord(String word) {
        stopWords.add(word);
    }

    public String[] removeStopWords(String[] words) {
        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(words));
        for (int i = 0; i < tokens.size(); i++) {
//            System.out.println(stopWords.contains(tokens.get(i)) + " " + tokens.get(i));
            if (stopWords.contains(tokens.get(i))) {
                tokens.remove(i);
            }
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    public String removeStopWords(String words) {
        String arr[] = WhitespaceTokenizer.INSTANCE.tokenize(words);
        StringBuilder sb = new StringBuilder();
//        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(arr));
        for (int i = 0; i < arr.length; i++) {
//            System.out.println(tokens.get(i) + "-");
            if (stopWords.contains(arr[i])) {
//                tokens.remove(i);
//                System.out.println("Removing: [" + arr[i] + "]");
            } else {
                sb.append(arr[i]+" ");
            }
        }
        return sb.toString();
    }

    public void displayStopWords() {
        Iterator<String> iterator = stopWords.iterator();
        while (iterator.hasNext()) {
            System.out.print("[" + iterator.next() + "]  ");
        }
    }
}
