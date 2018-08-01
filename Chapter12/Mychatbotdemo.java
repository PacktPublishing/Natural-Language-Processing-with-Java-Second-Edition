/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter12;

import java.io.File;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

/**
 *
 * @author ashish
 */
class MyChat{
    
}

public class Mychatbotdemo {
    private static final boolean TRACE_MODE = false;
    static String botName = "appointment";
    private static String getResourcePath(){
        File currDir = new File(".");
        String path = currDir .getAbsolutePath();
        path = path.substring(0, path.length()-2);
        System.out.println(path);
            String resourcePath = path + File.separator  + "src/chapter12/mybot";
        return resourcePath;
    }
    public static void main(String args[]){
        try
        {
            String resourcePath = getResourcePath();
            System.out.println(resourcePath);
            MagicBooleans.trace_mode = TRACE_MODE;
            Bot bot = new Bot(botName, resourcePath);
            Chat chatSession = new Chat(bot);
            bot.brain.nodeStats();
            String textLine = "";
            System.out.println("Robot : Hello, I am your appointment scheduler May i know your name");
            while(true){
                
                System.out.println("Human : ");
                textLine = IOUtils.readInputTextLine();
                if ((textLine==null) || (textLine.length()<1)){
                    textLine = MagicStrings.null_input;
                }
                if(textLine.equals("q")){
                    System.exit(0);
                } else if (textLine.equals("wq")){
                    bot.writeQuit();
                } else {
                    String request = textLine;
                    if(MagicBooleans.trace_mode)
                        System.out.println("STATE=" + request + ":THAT" + ((History)chatSession.thatHistory.get(0)).get(0) + ": Topic" + chatSession.predicates.get("topic"));
                    String response = chatSession.multisentenceRespond(request);
                    while(response.contains("&lt;"))
                        response = response.replace("&lt;", "<");
                    while(response.contains("&gt"))
                        response = response.replace("&gt;", ">");
                    System.out.println("Robot : " + response);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
