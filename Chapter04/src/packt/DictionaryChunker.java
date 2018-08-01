package packt;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.dict.Dictionary;
import com.aliasi.dict.ExactDictionaryChunker;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import java.util.Iterator;
import java.util.Set;

public class DictionaryChunker {
    private static final String sentences[] = {"Joe was the last person to see Fred. ",
        "He saw him in Boston at McKenzie's pub at 3:00 where he paid "
        + "$2.45 for an ale. ",
        "Joe wanted to go to Vermont for the day to visit a cousin who "
        + "works at IBM, but Sally and he had to look for Fred"};
    static final double CHUNK_SCORE = 1.0;

    public static void main(String[] args) {

        MapDictionary<String> dictionary = new MapDictionary<String>();
        dictionary.addEntry(new DictionaryEntry<String>("Joe","PERSON",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Fred","PERSON",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Boston","PLACE",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("pub","PLACE",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Vermont","PLACE",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("IBM","ORGANIZATION",CHUNK_SCORE));
        dictionary.addEntry(new DictionaryEntry<String>("Sally","PERSON",CHUNK_SCORE));


        ExactDictionaryChunker dictionaryChunkerTT
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         true,true);

        ExactDictionaryChunker dictionaryChunkerTF
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         true,false);

        ExactDictionaryChunker dictionaryChunkerFT
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         false,true);

        ExactDictionaryChunker dictionaryChunkerFF
            = new ExactDictionaryChunker(dictionary,
                                         IndoEuropeanTokenizerFactory.INSTANCE,
                                         false,false);



        System.out.println("\nDICTIONARY\n" + dictionary);

        for (int i = 0; i < sentences.length; ++i) {
            String text = sentences[i];
            System.out.println("\n\nTEXT=" + text);

            chunk(dictionaryChunkerTT,text);
            chunk(dictionaryChunkerTF,text);
            chunk(dictionaryChunkerFT,text);
            chunk(dictionaryChunkerFF,text);
        }

    }

    static void chunk(ExactDictionaryChunker chunker, String text) {
        System.out.println("\nChunker."
                           + " All matches=" + chunker.returnAllMatches()
                           + " Case sensitive=" + chunker.caseSensitive());
        Chunking chunking = chunker.chunk(text);
        for (Chunk chunk : chunking.chunkSet()) {
            int start = chunk.start();
            int end = chunk.end();
            String type = chunk.type();
            double score = chunk.score();
            String phrase = text.substring(start,end);
            System.out.println("     phrase=|" + phrase + "|"
                               + " start=" + start
                               + " end=" + end
                               + " type=" + type
                               + " score=" + score);
        }
    }

}

