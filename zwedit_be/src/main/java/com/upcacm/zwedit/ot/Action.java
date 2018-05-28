package com.upcacm.zwedit.ot;

public class Action {

    public int intVal = 0;
     
    public String stringVal = "";
    
    public ActionType type;     

    public Action(ActionType type, String stringVal) {
        this.type = type;
        this.stringVal = stringVal;
    }
     
    public Action(ActionType type, int intVal) {
     	this.type = type;
        this.intVal = intVal;
    }
     
    public int length() {
        int res = 0;
        switch (this.type) {  
            case RETAIN:  
                res = this.intVal;
                break;  
            case DELETE:  
                res = -this.intVal; 
                break;  
            case INSERT:
                res = this.stringVal.length(); 
                break;  
        }
        return res;  
    }

    public Action _shorten(int by) {
        Action res = null;
        switch (this.type) {
            case RETAIN:
                res = new Action(this.type, this.intVal - by);
                break;
            case DELETE:
                res = new Action(this.type, this.intVal + by);
                break;
            case INSERT:
                res = new Action(this.type, this.stringVal.substring(by));
                break;
        }
        return res;
    }
     
     
    public Action add(Action a) {
        Action res = null;
        switch (this.type) {
            case RETAIN:
                res = new Action(this.type, this.intVal + a.intVal);
                break;
            case DELETE:
                res = new Action(this.type, this.intVal + a.intVal);
                break;
            case INSERT:
                res = new Action(this.type, this.stringVal + a.stringVal);
                break;
        }
        return res; 
    }
}
