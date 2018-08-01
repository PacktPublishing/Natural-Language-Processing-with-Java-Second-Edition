package packt;

import java.io.File;
import java.io.IOException;

import com.aliasi.chunk.CharLmHmmChunker;
//import com.aliasi.corpus.parsers.Muc6ChunkParser;
import com.aliasi.hmm.HmmCharLmEstimator;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;

@SuppressWarnings("deprecation")
public class TrainEntities {

    static final int MAX_N_GRAM = 50;
    static final int NUM_CHARS = 300;
    static final double LM_INTERPOLATION = MAX_N_GRAM; // default behavior

    public static void main(String[] args) throws IOException {
        File corpusFile = new File("inputfile.txt");// my annotated file
        File modelFile = new File("outputmodelfile.model"); 

        System.out.println("Setting up Chunker Estimator");
        TokenizerFactory factory
            = IndoEuropeanTokenizerFactory.INSTANCE;
        HmmCharLmEstimator hmmEstimator
            = new HmmCharLmEstimator(MAX_N_GRAM,NUM_CHARS,LM_INTERPOLATION);
        CharLmHmmChunker chunkerEstimator
            = new CharLmHmmChunker(factory,hmmEstimator);

        System.out.println("Setting up Data Parser");
//        Muc6ChunkParser parser = new Muc6ChunkParser();  
//        parser.setHandler( chunkerEstimator);

        System.out.println("Training with Data from File=" + corpusFile);
//        parser.parse(corpusFile);

        System.out.println("Compiling and Writing Model to File=" + modelFile);
        AbstractExternalizable.compileTo(chunkerEstimator,modelFile);
    }

}