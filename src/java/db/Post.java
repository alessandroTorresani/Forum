/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;

/**
 *
 * @author Alessandro
 */
public class Post implements Serializable {

    private int group_id;
    private int writer_id;
    private int post_id;
    private String date;
    private String content;
    private String writer_name;

    public int getGroupId() {
        return this.group_id;
    }

    public void setGroupId(int group_id) {
        this.group_id = group_id;
    }

    public int getWriterId() {
        return this.writer_id;
    }

    public void setWriterId(int writer_id) {
        this.writer_id = writer_id;
    }

    public String getDate() {
        return this.date;
    }

    public void SetDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriterName() {
        return this.writer_name;
    }

    public void setWriterName(String writer_name) {
        this.writer_name = writer_name;
    }
    
    public int getPostId(){
        return this.post_id;
    }
    
    void setPostId(int post_id){
        this.post_id = post_id;
    }

}
