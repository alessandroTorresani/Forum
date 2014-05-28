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
public class User implements Serializable{
    private String username;
    private String password;
    private int user_id;
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public int getUserId(){
        return this.user_id;
    }
    
    public void setUserId(int user_id){
        this.user_id = user_id;
    }
}
 
    

