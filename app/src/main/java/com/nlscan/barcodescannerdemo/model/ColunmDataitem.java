package com.nlscan.barcodescannerdemo.model;

/**
 * Created by fairoze khazi on 22/02/2017.
 */

public class ColunmDataitem {

    private String Id;

    private String projectname;

    public ColunmDataitem(String projectname) {

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
