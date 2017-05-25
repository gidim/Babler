import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Sania on 2/9/16.
 */
public class TestResources {

    @Test
    public void loadFileFromTestResources()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("configFromFile.properties").getFile());
    }

}
