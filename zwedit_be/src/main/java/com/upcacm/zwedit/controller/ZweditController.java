package com.upcacm.zwedit.controller;

import java.util.concurrent.ConcurrentHashMap;

import com.upcacm.zwedit.util.RedisPool;

import redis.clients.jedis.Jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZweditController {
     
    private final Logger logger = LoggerFactory.getLogger(ZweditController.class);

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
        if (!store.containsKey(editRoomUrl)) {
            try {
                Jedis jr = RedisPool.getJedis();
                boolean ret =  jr.exists(editRoomUrl).booleanValue();
                if (ret) store.put(editRoomUrl, new EditRoom(editRoomUrl));
                RedisPool.returnResource(jr);
                return ret;
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
     
    public void setStore(ConcurrentHashMap<String, EditRoom> newStore) {
        this.store = newStore;
    }
     
    public void createEditRoom(String editRoomUrl) {
        store.put(editRoomUrl, new EditRoom(editRoomUrl));
    }
     
    public EditRoom getEditRoom(String editRoomUrl) {
	    return store.get(editRoomUrl);
    }
}
