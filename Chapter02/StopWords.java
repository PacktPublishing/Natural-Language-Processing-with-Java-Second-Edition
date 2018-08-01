package packt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class StopWords {

    private String[] defaultStopWords = {"i", "a", "about", "an", "are", "as", "at", 
        "be", "by", "com", "for", "from", "how", "in", "is", "it", "of", "on", 
        "or", "that", "the", "this", "to", "was", "what", "when", "where", 
        "who", "will", "with"};
    
    private static HashSet stopWords  = new HashSet();

    public StopWords() {
        stopWords.addAll(Arrays.asList(defaultStopWords));
    }

    public StopWords(String fileName) {
        try {
            BufferedReader bufferedreader = 
                    new BufferedReader(new FileReader(fileName));
            while (bufferedreader.ready()) {
                stopWords.add(bufferedreader.readLine());
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

    public void displayStopWords() {
        Iterator<String> iterator = stopWords.iterator();
        while(iterator.hasNext()) {
            System.out.print("[" + iterator.next() + "]  ");
        }
    }
}
