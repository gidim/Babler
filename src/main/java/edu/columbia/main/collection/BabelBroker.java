package edu.columbia.main.collection;

import org.apache.log4j.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gideon on 4/8/15.
 */


/**
 * A blocking queue for communication between producers consumer threads
 */
public class BabelBroker {

    Logger log = Logger.getLogger(BabelBroker.class);

    public ArrayBlockingQueue <BabelJob> queue = new ArrayBlockingQueue(100);


    public void put(BabelJob data) throws InterruptedException
    {
        this.queue.put(data);
        log.info("Broker Size: "+queue.size());
    }

    public BabelJob get() throws InterruptedException
    {
        return this.queue.poll(1, TimeUnit.SECONDS);
    }


}
