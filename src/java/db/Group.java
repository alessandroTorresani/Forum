/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;

/**
 *
 * @author Ale
 */
public class Group implements Serializable {

    private String groupName;
    private String creationDate; 
    private int owner;
    private String owner_name;
    private int group_id;

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
   
    public String getOwnerName(){
        return this.owner_name;
    }
    
    public void setOwnerName(String owner_name){
        this.owner_name = owner_name;
    }
    
    public int getGroupId(){
        return this.group_id;
    }
    
    public void setGroupId(int group_id){
        this.group_id = group_id;
    }

}
