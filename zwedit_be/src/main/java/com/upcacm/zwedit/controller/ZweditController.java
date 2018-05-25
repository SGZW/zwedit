package com.upcacm.zwedit.controller;

import java.util.concurrent.ConcurrentHashMap;

public class ZweditController {
     
     private volatile static ZweditController zweditController;
     
     private ConcurrentHashMap<String, EditRoom> store = null;     

     private ZweditController() {
         store = new ConcurrentHashMap<String, EditRoom>();
     }
     
     public static ZweditController getZweditController() {
         if(zweditController == null) {
            synchronized(ZweditController.class) {
                if(zweditController == null) {
                    zweditController = new ZweditController();
                }
            }
        }
        return zweditController;
     }
     
     public boolean exists(String editRoomUrl) {
	return store.containsKey(editRoomUrl);
     }
     
     public void setStore(ConcurrentHashMap<String, EditRoom> newStore) {
        this.store = newStore;
     }
     
     public void createEditRoom(String editRoomUrl) {
     	store.put(editRoomUrl, new EditRoom());
     }
     
     public EditRoom getEditRoom(String editRoomUrl) {
	return store.get(editRoomUrl);
     }
}
