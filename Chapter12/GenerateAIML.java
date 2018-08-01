package chapter12;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import org.alicebot.ab.Bot;
import org.alicebot.ab.MagicBooleans;

/**
 *
 * @author ashish
 */
public class GenerateAIML {
    
        private static final boolean TRACE_MODE = false;
        static String botName = "appointment";
 
    public static void main(String[] args) {
        try {
 
            String resourcesPath = getResourcesPath();
            System.out.println(resourcesPath);
            MagicBooleans.trace_mode = TRACE_MODE;
            Bot bot = new Bot("appointment", resourcesPath);
             
            bot.writeAIMLFiles();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private static String getResourcesPath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter12/mybot";
        return resourcePath;
    }
}
    
    

