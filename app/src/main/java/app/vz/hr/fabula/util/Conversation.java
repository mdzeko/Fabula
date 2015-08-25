package app.vz.hr.fabula.util;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by miso on 8/22/15
 */
public class Conversation {
    public static View getContactList(Database database, final String myPhone){
        View phoneView = database.getView("contacts");
        phoneView.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if (document.get("to").equals(myPhone) && !document.get("to").toString().isEmpty())
                    emitter.emit(document.get("name"), document.get("from"));
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                List<Object> nonDuplicateKeys = new ArrayList<>();
                List<Object> nonDuplicateVals = new ArrayList<>();
                for(Object key : keys)
                    if(!nonDuplicateKeys.contains(key))
                        nonDuplicateKeys.add(key);
                for(Object value : values)
                    if(!nonDuplicateVals.contains(value))
                        nonDuplicateVals.add(value);
                return nonDuplicateVals;
            }
        }, "1");
        return phoneView;
    }
    public static View getMessages(Database database, final String contact, final String myPhone){
        View messagesView = database.getView("messages");
        messagesView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if((document.get("to").equals(contact) && document.get("from").equals(myPhone)) || (document.get("from").equals(contact) && document.get("to").equals(myPhone))){
                    emitter.emit(document.get("datetime"), document);
                }
            }
        }, "2");
        return messagesView;
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
