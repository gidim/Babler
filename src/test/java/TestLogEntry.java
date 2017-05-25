import edu.columbia.main.db.Models.LogEntry;
import org.junit.*;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static com.mongodb.client.model.Filters.*;
/**
 * Created by Gideon on 9/24/15.
 */
public class TestLogEntry {


    @Test
    public void testDifferentLinesFromLog(){

        //id: 569295949648027650, url:http://twitter.com/465714163/status/569295949648027650

        LogEntry entry = new LogEntry("id: 569295949648027650, url:http://twitter.com/465714163/status/569295949648027650");
        Assert.assertTrue(entry.getFileName().equals("569295949648027650"));
        Assert.assertTrue(entry.getUrl().equals("http://twitter.com/465714163/status/569295949648027650"));

        //id: 569295949648027650#, url:http://twitter.com/465714163/status/56929594964802765
        entry = new LogEntry("id: 569295949648027650#, url:http://twitter.com/465714163/status/56929594964802765");
        Assert.assertTrue(entry.getFileName().equals("569295949648027650"));
        Assert.assertTrue(entry.getUrl().equals("http://twitter.com/465714163/status/56929594964802765"));
        Assert.assertNull(entry.getGuid());

        //blog post
        //id: -3a16ea81+0d425e8f#-163167452, url:http://ephremzlideta.blogspot.com/feeds/posts/default
        entry = new LogEntry("id: -3a16ea81+0d425e8f#-163167452, url:http://ephremzlideta.blogspot.com/feeds/posts/default");
        Assert.assertTrue(entry.getFileName().equals("-163167452"));
        Assert.assertTrue(entry.getUrl().equals("http://ephremzlideta.blogspot.com/feeds/posts/default"));
        Assert.assertTrue(entry.getGuid().equals("-3a16ea81+0d425e8f"));


        //forum post
        //id: 9/2128/8#-1001461034, url:www.lietuviai.se/frm/viewtopic.php?f=9
        entry = new LogEntry("9/2128/8#-1001461034, url:www.lietuviai.se/frm/viewtopic.php?f=9");
        Assert.assertTrue(entry.getFileName().equals("-1001461034"));
        Assert.assertTrue(entry.getUrl().equals("www.lietuviai.se/frm/viewtopic.php?f=9"));
        Assert.assertTrue(entry.getGuid().equals("9/2128/8"));


    }


}
