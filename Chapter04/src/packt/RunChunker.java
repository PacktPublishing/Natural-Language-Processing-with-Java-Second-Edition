 package packt;

import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;

import com.aliasi.util.AbstractExternalizable;

import java.io.File;

public class RunChunker {
    private static final String sentences[] = {"Joe was the last person to see Fred. ",
        "He saw him in Boston at McKenzie's pub at 3:00 where he paid "
        + "$2.45 for an ale. ",
        "Joe wanted to go to Vermont for the day to visit a cousin who "
        + "works at IBM, but Sally and he had to look for Fred"};
    
    public static void main(String[] args) throws Exception {
	File modelFile = new File(getModelDir(), "ne-en-news-muc6.AbstractCharLmRescoringChunker");

	System.out.println("Reading chunker from file=" + modelFile);
	Chunker chunker 
	    = (Chunker) AbstractExternalizable.readObject(modelFile);

	for (int i = 0; i < sentences.length; ++i) {
	    Chunking chunking = chunker.chunk(sentences[i]);
	    System.out.println("Chunking=" + chunking);
	}

    }
    
    public static File getModelDir() {
        return new File("C:\\Current Books in Progress\\NLP and Java\\Models");
    }
}

