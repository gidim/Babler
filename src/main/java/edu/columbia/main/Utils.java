package edu.columbia.main;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gideon on 3/6/15.
 */
public class Utils {

    public static void printWithTime(String str){

        Logger log = Logger.getLogger("Utils");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdf.format(now);

        log.info(strDate + " | " + str );
    }

    public static String removePuntuation(String s)
    {
         StringBuilder sb = new StringBuilder();
         char[] punc = "',.;!?(){}[]<>%@#$%^&*".toCharArray();

        String tmp;
        boolean fl=true;

        for(int i=0;i<s.length();i++)
        {
            fl=true;
            char strChar=s.charAt(i);
            for (char badChar : punc)
            {
                if (badChar == strChar)
                {
                    fl=false;
                    break;
                }
            }

            if(fl)
            {
                sb.append(strChar);
            }
        }
        return sb.toString();
    }
}
