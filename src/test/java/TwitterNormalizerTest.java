import edu.columbia.main.normalization.TwitterNormalizer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gideon on 9/30/15.
 */
public class TwitterNormalizerTest {


    //@Test
    public void testEmojiRemover() throws IOException {

        Logger log = Logger.getLogger(TwitterNormalizerTest.class);

        File folder = new File("/Users/Gideon/Documents/dev/Babel/scraping/lit/twitter");
        for (File tweetFile : folder.listFiles()) {
            String origData = FileUtils.readFileToString(tweetFile, "UTF-8");
            String data = new TwitterNormalizer().removeEmojis(origData);
            if (!origData.equals(data)) {
                log.info(origData);
                log.info(data);
            }
        }
    }



}
