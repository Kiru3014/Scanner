package com.nlscan.barcodescannerdemo.model;

import android.content.Intent;

/**
 * Created by fairoze khazi on 22/02/2017.
 */

public class ColunmData {

    private String Id;

    private String projectname;

    public ColunmData(String projectname) {

        super();
        this.projectname = projectname;
    }



    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }
}
