package com.example.covid1;

import java.sql.Date;
import com.google.firebase.Timestamp;


public class People {

    private int noofpeople;
    private String time;




    public People() {


    }



    public People(int noofpeople, String time) {
        this.noofpeople = noofpeople;

        this.time = time;

    }

    public int getNoofpeople() {
        return noofpeople;
    }

    public void setNoofpeople(int noofpeople) {
        this.noofpeople = noofpeople;
    }




    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
