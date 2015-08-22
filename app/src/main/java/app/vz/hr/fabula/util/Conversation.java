package app.vz.hr.fabula.util;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miso on 8/22/15
 */
public class Conversation {
    private Document doc;

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public static List<QueryRow> getAllConversations(Database database){
        List<QueryRow> returnList = new ArrayList<>();
        Query q = database.createAllDocumentsQuery();
        q.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator enumerator = q.run();
            if(enumerator.getCount() != 0){
                while (enumerator.hasNext()){
                    returnList.add(enumerator.next());
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return returnList;
    }
}
