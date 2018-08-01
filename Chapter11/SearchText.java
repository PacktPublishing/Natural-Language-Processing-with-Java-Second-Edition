/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 *
 * @author ashish
 */
class StopWords
{
    
    public String removeStopWords(String words) {
        String arr[] = WhitespaceTokenizer.INSTANCE.tokenize(words);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (words.contains(arr[i])) {
                // Do nothing
            } else {
                sb.append(arr[i]+" ");
            }
        }
        return sb.toString();
    }
}
public class SearchText {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        String resourcePath = path + File.separator  + "src/chapter11/";
        return resourcePath;
    }
    
    public static void main(String args[]){
        try {
            InputStream is = new FileInputStream(new File(getResourcePath() + "en-sent.bin"));
            FileReader fr = new FileReader(getResourcePath() + "pg164.txt");
            BufferedReader br = new BufferedReader(fr);
            System.out.println(getResourcePath() + "en-sent.bin");
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME detector = new SentenceDetectorME(model);
            
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine())!=null){
                sb.append(line + " ");
            }
            String sentences[] = detector.sentDetect(sb.toString());
            for (int i = 0; i < sentences.length; i++) {
                sentences[i] = sentences[i].toLowerCase();
            }
            
//            StopWords stopWords = new StopWords("stop-words_english_2_en.txt");
//            for (int i = 0; i < sentences.length; i++) {
//                sentences[i] = stopWords.removeStopWords(sentences[i]);
//            }
            
            HashMap<String, Word> wordMap = new HashMap();
            for (int sentenceIndex = 0; sentenceIndex < sentences.length; sentenceIndex++) {
            String words[] = WhitespaceTokenizer.INSTANCE.tokenize(sentences[sentenceIndex]);
            Word word;
            for (int wordIndex = 0; 
                    wordIndex < words.length; wordIndex++) {
                String newWord = words[wordIndex];
                if (wordMap.containsKey(newWord)) {
                     word = wordMap.remove(newWord);
                } else {
                    word = new Word();
                }
                word.addWord(newWord, sentenceIndex, wordIndex);
                wordMap.put(newWord, word);
            }
//            for(String k : wordMap.keySet()){
//                System.out.println(k);
//            }
            Word sword = wordMap.get("sea");
            ArrayList<Positions> positions = sword.getPositions();
            for (Positions position : positions) {
                System.out.println(sword.getWord() + " is found at line " 
                    + position.sentence + ", word " 
                    + position.position);
            }
        }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearchText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
