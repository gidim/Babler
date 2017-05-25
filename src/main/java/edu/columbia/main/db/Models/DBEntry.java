package edu.columbia.main.db.Models;

import com.mongodb.BasicDBObject;
import org.apache.log4j.Logger;

import javax.swing.plaf.basic.BasicArrowButton;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Gideon on 9/24/15.
 */

/**
 * Master class for DB POJO models
 */
public class DBEntry extends BasicDBObject {

    Logger log = Logger.getLogger(DBEntry.class);

    public DBEntry(String origFileName){
        this.put("origFileName",origFileName);
    }

    public DBEntry(BasicDBObject obj){
        super(obj.toMap());
    }
    public String getId(){
        return (String)get("_id");
    }

    public String getFileName() {
        return (String) get("origFileName");
    }

    public void setFileName(String origFileName) {
        put("origFileName",origFileName);
    }

    public String getData(){
        return (String)get("data");
    }

    public void setData(String data) {
        if(data == null || data == "")
            throw new NullPointerException("Data is empty");

        setIdByData(data);
        put("data",data);
    }

    /**
     * Generates a unique sha256 id based on the content of the DBEntry
     * @param data
     */
    private void setIdByData(String data) {
        String hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(data);
        put("_id",hash);
    }


    /**
     * Verifies that this entry is valid
     * @return true if valid
     */
    public boolean isValid() {
        Iterator it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue() == null || pair.getValue().equals("")) {
                log.error("DBEntry not valid on " + pair.getKey() + ": " + pair.getValue());
                return false;
            }
        }
        return true;
    }
}
