package com.upcacm.zwedit.ot;

import java.util.ArrayList;

import com.upcacm.zwedit.util.Pair;

public class TextOperation {
   
    public ArrayList<Action> actions = null;

    public TextOperation() {
        actions = new ArrayList<Action>();
    }
     
    public int len_difference() {
    	int ret = 0;
        for (Action action: actions) {
            switch (action.type) {
                case RETAIN:
                    break;
                case DELETE:
                    ret -= action.length();
                    break;
                case INSERT:
                    ret += action.length();
                    break;
            }
        }
        return ret;
    }
    
    public void retain(Action a) {
    	if (a.length() == 0) return;
        int sz = actions.size();
        if (sz > 0 && actions.get(sz - 1).type == ActionType.RETAIN) {
            Action na = actions.get(sz - 1).add(a);
            actions.set(sz - 1, na);    
        } else actions.add(a);
    }
    
    public void insert(Action a) {
        if (a.length() == 0) return;
        int sz = actions.size();
        if (sz > 0 && actions.get(sz - 1).type == ActionType.INSERT) {
            Action na = actions.get(sz - 1).add(a);
            actions.set(sz - 1, na);
        } else if (sz > 0 && actions.get(sz - 1).type == ActionType.DELETE) {
            if (sz > 1 && actions.get(sz - 2).type == ActionType.INSERT) {
                Action na = actions.get(sz - 2).add(a);
                actions.set(sz - 2, na);
            } else {
                Action tmp = actions.get(sz - 1);
                actions.set(sz - 1, a);
                actions.add(tmp);
            }
        } else actions.add(a);
    }
    
    public void delete(Action a) {
        if (a.length() == 0) return;
        int sz = actions.size();
        if (sz > 0 && actions.get(sz - 1).type == ActionType.DELETE) {
            Action na = actions.get(sz - 1).add(a);
            actions.set(sz - 1, a);
        } else actions.add(a);
    }
   
    public String apply(String doc) throws Exception {
        int i = 0;
        ArrayList<String> parts = new ArrayList<String>();
        for (Action action: actions) {
            switch (action.type) {
                case RETAIN:
                    if (i + action.length() > doc.length()) {
                        throw new Exception("Cannot apply operation: operation is too long.");
                    }
                    parts.add(doc.substring(i, (i + action.length())));
                case DELETE:
                    i += action.length();
                    if (i > doc.length()) {
                        throw new Exception("Cannot apply operation: operation is too long.");
                    }
                    break;
                case INSERT:
                    parts.add(action.stringVal);
                    break;
            }
        }
        if (i != doc.length()) {
            throw new Exception("Cannot apply operation: operation is too short.");
        }
        String ret = String.join("", parts);
        return ret;
    }
    
    public static boolean is_retain(Action action) { 
        return action.type == ActionType.RETAIN; 
    }
        
    public static boolean is_insert(Action action) {
        return action.type == ActionType.INSERT;
    }
    
    public static boolean is_delete(Action action) {
        return action.type == ActionType.DELETE;
    }

    public static Pair<Action, Action> shorten_op(Action a, Action b) {
        if (a.length() == b.length()) {
            a = null;
            b = null;
        } else if (a.length() > b.length()) {
            a = a._shorten(b.length());
            b = null;
        } else {
            a = null;
            b = b._shorten(a.length());
        }
        return new Pair(a, b);
    }
    
    public TextOperation compose(TextOperation other) throws Exception {
        TextOperation ret = new TextOperation();
        int i = -1, j = -1;
        Action a = null;
        Action b = null;
        while (true) {
            if (a == null) {
                ++i;
                if (i < this.actions.size()) a = this.actions.get(i);
            }
            if (b == null) {
                ++j;
                if (j < other.actions.size()) b = other.actions.get(j);
            }
            
            if (a == null && b == null) break;
            
            if (is_delete(a)) {
                ret.delete(a);
                a = null;
                continue;
            }
            if (is_insert(b)) {
                ret.insert(b);
                b = null;
                continue;
            }
            
            if (a == null) throw new Exception("Cannot compose operations: first operation is too short");
            if (b == null) throw new Exception("Cannot compose operations: first operation is too long");
            
            int min_len = Math.min(a.length(), b.length());
            if (is_retain(a) && is_retain(b)) {
                ret.retain(new Action(ActionType.RETAIN, min_len));
            } else if (is_insert(a) && is_retain(b)) {
                ret.insert(new Action(ActionType.INSERT, a.stringVal.substring(0, min_len)));
            } else if (is_retain(a) && is_delete(b)) {
                ret.delete(new Action(ActionType.DELETE, min_len));
            }
            
            Pair<Action, Action> p = shorten_op(a, b);
            a = p.getA();
            b = p.getB();
        }
        return ret; 
    }

    public static Pair<TextOperation, TextOperation> transform(TextOperation opA, TextOperation opB) throws Exception {
        int i = -1, j = -1;
        Action a = null;
        Action b = null;
        TextOperation transA = new TextOperation();
        TextOperation transB = new TextOperation();
        while (true) {
            if (a == null) {
                ++i;
                if (i < opA.actions.size()) a = opA.actions.get(i);
            }
            if (b == null) {
                ++j;
                if (j < opB.actions.size()) b = opB.actions.get(j);
            }
            
            if (a == null && b == null) break;
            
            if (is_insert(a)) {
                transA.insert(a);
                transB.retain(new Action(ActionType.RETAIN, a.length()));
                a = null;
                continue;
            }
            if(is_insert(b)) {
                transA.retain(new Action(ActionType.RETAIN, b.length()));
                transB.insert(b);
                b = null;
                continue;   
            }

            if (a == null) throw new Exception("Cannot transform operations: first operation is too short");
            if (b == null) throw new Exception("Cannot transform operations: first operation is too long");
            
            int min_len = Math.min(a.length(), b.length());
            if (is_retain(a) && is_retain(b)) {
                Action na = new Action(ActionType.RETAIN, min_len);
                transA.retain(na);
                transB.retain(na);
            } else if (is_delete(a) && is_retain(b)) {
                transA.delete(new Action(ActionType.DELETE, min_len));
            } else if (is_retain(a) && is_delete(b)) {
                transB.delete(new Action(ActionType.DELETE, min_len));
            }

            Pair<Action, Action> p = shorten_op(a, b);
            a = p.getA();
            b = p.getB();
        }
        return new Pair(transA, transB);
    } 
}
