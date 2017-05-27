package edu.columbia.main.screen_logging;

import java.util.HashMap;

/**
 * Created by Gideon on 26/05/2017.
 */
public class ViewManager {

    HashMap<String,TaskLogger> taskLoggers = new HashMap<>();

    public ViewManager(String [] languages) {
        for(String lang : languages){
            taskLoggers.put(lang, new TaskLogger(lang));
        }
    }

    public TaskLogger getLogger(String lang){
        return this.taskLoggers.get(lang);
    }


    public void printToConsole(){
        clearScreen();
        for(TaskLogger logger : this.taskLoggers.values()){
            System.out.println(logger);
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
