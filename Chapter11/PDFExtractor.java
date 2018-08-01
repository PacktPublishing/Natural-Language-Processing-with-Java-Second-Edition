/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author ashish
 */
public class PDFExtractor {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        String resourcePath = path + File.separator  + "src/chapter11/TestDocument.pdf";
        return resourcePath;
    }
    public static void main(String args[]){
        try{
        File file = new File(getResourcePath());
        PDDocument pd = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text= stripper.getText(pd);
        System.out.println(text);
        }
        catch(IOException ex){
            System.out.println(ex);
        }
    }
}
