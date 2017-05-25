import edu.columbia.main.YouTube.YouTubeCaptionsScraper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Gideon on 2/17/16.
 */
public class TestYouTube {


    @Test
    public void testVideoHasCaptionsInLanguage() throws IOException {
        YouTubeCaptionsScraper yt = new YouTubeCaptionsScraper("amh");
        Assert.assertTrue(yt.videoHasCaptionsInLanguage("ycentbQV_oc","amh"));
    }

    @Test
    public void testgetAndSaveTranscript() throws IOException {
        YouTubeCaptionsScraper yt = new YouTubeCaptionsScraper("amh");
        yt.getAndSaveTranscript("ycentbQV_oc","amh");
    }


}
