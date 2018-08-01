/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.l3s.boilerpipe.sax.HTMLDocument;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ashish
 */
public class HTMLExtractorDemo {
    public static void main(String args[]){
        try{
            URL url = new URL("https://en.wikipedia.org/wiki/Berlin");
            HTMLDocument htmldoc = HTMLFetcher.fetch(url);
            InputSource is = htmldoc.toInputSource();
            TextDocument document = new BoilerpipeSAXInput(is).getTextDocument();
            System.out.println(document.getText(true, true));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
            Logger.getLogger(HTMLExtractorDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(HTMLExtractorDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | BoilerpipeProcessingException ex) {
            System.out.println(ex);
            Logger.getLogger(HTMLExtractorDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
