package com.upcacm.zwedit.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import com.upcacm.zwedit.ot.Action;
import com.upcacm.zwedit.ot.ActionType;
import com.upcacm.zwedit.ot.TextOperation;

public class JsonUtil {

    public static Pair<Integer, TextOperation> loadReq(String rawJson) throws Exception {
         ContainerFactory containerFactory = new ContainerFactory(){
            public List creatArrayContainer() {
                return new LinkedList();
            }
            public Map createObjectContainer() {
                return new LinkedHashMap();
            } 
        };
        try {
            JSONParser parser = new JSONParser();
            Map mp  = (Map)parser.parse(rawJson, containerFactory);
            Long re = (Long)mp.get("revision");
            Integer revision = new Integer(re.intValue());
            LinkedList actions = (LinkedList)mp.get("actions");
            TextOperation textOp = new TextOperation();
            for (int i = 0; i < actions.size(); ++i) {
                Object a = actions.get(i);
                if (a instanceof Long) {
                    Long b = (Long)a;
                    int intVal = b.intValue();
                    if(intVal == 0) continue;
                    else if(intVal > 0) textOp.retain(new Action(ActionType.RETAIN, intVal));
                    else textOp.delete(new Action(ActionType.DELETE, intVal));
                } else if (a instanceof Integer) {
                    Integer b = (Integer)a;
                    int intVal = b.intValue();
                    if(intVal == 0) continue;
                    else if(intVal > 0) textOp.retain(new Action(ActionType.RETAIN, intVal));
                    else textOp.delete(new Action(ActionType.DELETE, intVal));
                } else if (a instanceof String) {
                    String b = (String)a;
                    if(b.length() != 0) textOp.insert(new Action(ActionType.INSERT, b));
                }
            }
            return new Pair(revision, textOp);
        } catch (Exception e) {
            throw new Exception("json parse failed: " + e.getMessage());
        }
    }
    
    public static String dumpMap(LinkedHashMap<String, Object> map) throws Exception {
        try {
            return JSONValue.toJSONString(map);
        } catch (Exception e) {
            throw new Exception("dump map to json failed: " + e.getMessage());
        }
    }
}
