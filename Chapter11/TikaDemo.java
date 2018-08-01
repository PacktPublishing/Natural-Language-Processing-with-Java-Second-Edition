/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author ashish
 */
public class TikaDemo {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        String resourcePath = path + File.separator  + "src/chapter11/TestDocument.pdf";
        return resourcePath;
    }
    public static void main(String args[]){
        Tika tika = new Tika();
        try{
            File file = new File(getResourcePath());            
            String filetype = tika.detect(file);
            
            System.out.println(filetype);
            System.out.println(tika.parseToString(file));
            
            
        } catch (IOException ex) {
            Logger.getLogger(TikaDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TikaException ex) {
            Logger.getLogger(TikaDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
