/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.io.Serializable;

/**
 *
 * @author harwin
 */
public class Bid implements Serializable {
    private int bid_id;
    private int user_id;
    private int group_id;
    private int sender_id;
    private String sender_name;
    private String group_name;
    
    public int getBidId(){
        return this.bid_id;
    }
    
    public void setBidId(int bid_id){
        this.bid_id = bid_id;
    }
    
    public int getUserId(){
        return this.user_id;
    }
    
    public void setUserId(int user_id){
        this.user_id = user_id;
    }
    
    public int getGroupId(){
        return this.group_id;
    }
    
    public void setGroupId(int group_id){
        this.group_id = group_id;
    }
    
    public int getSenderId(){
        return this.sender_id;
    }
    
    public void setSenderId(int sender_id){
        this.sender_id = sender_id;
    }
    
    public String getSenderName(){
        return this.sender_name;
    }
    
    public void setSenderName(String sender_name){
        this.sender_name = sender_name;
    }
    
    public String getGroupName(){
        return this.group_name;
    }
    
    public void setGroupName(String group_name){
        this.group_name = group_name;
    }
    
}
