/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.POITextExtractor;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.POIXMLProperties.ExtendedProperties;
import org.apache.poi.POIXMLPropertiesTextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

/**
 *
 * @author ashish
 */
public class WordDocExtractor {
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        String resourcePath = path + File.separator  + "src/chapter11/TestDocument.docx";
        return resourcePath;
    }
    public static void main(String args[]){
        try {
            FileInputStream fis = new FileInputStream(getResourcePath());
            POITextExtractor textExtractor = ExtractorFactory.createExtractor(fis);
            System.out.println(textExtractor.getText());
            
            POITextExtractor metaExtractor = textExtractor.getMetadataTextExtractor();
            System.out.println(metaExtractor.getText());
            fis = new FileInputStream(getResourcePath());
            POIXMLPropertiesTextExtractor properties = new POIXMLPropertiesTextExtractor(new XWPFDocument(fis));
            CoreProperties coreProperties = properties.getCoreProperties();
            System.out.println(properties.getCorePropertiesText());

            ExtendedProperties extendedProperties = properties.getExtendedProperties();
            System.out.println(properties.getExtendedPropertiesText());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OpenXML4JException | XmlException ex) {
            Logger.getLogger(WordDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
