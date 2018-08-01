/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter6;

import glove.GloVe;
import glove.objects.Cooccurrence;
import glove.objects.Vocabulary;
import glove.utils.Methods;
import glove.utils.Options;
import java.io.File;
import java.util.List;
import org.jblas.DoubleMatrix;

/**
 *
 * @author ashish
 */
public class GloveExample {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter6/";
        return resourcePath;
    }
    public static void main(String args[]){
        String file = getResourcePath() + "test.txt";
        
        Options options = new Options(); 
        options.debug = true;
        
        Vocabulary vocab = GloVe.build_vocabulary(file, options);
        
        options.window_size = 3;
        List<Cooccurrence> c =  GloVe.build_cooccurrence(vocab, file, options);
        
        options.iterations = 10;
        options.vector_size = 10;
        options.debug = true;
        DoubleMatrix W = GloVe.train(vocab, c, options);  

        List<String> similars = Methods.most_similar(W, vocab, "graph", 15);
        for(String similar : similars) {
            System.out.println("@" + similar);
        }
        
    }
    
}
