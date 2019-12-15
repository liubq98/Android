package com.example.liubq.httpapi;

public class Repo {
    private int id;
    private String name;
    private String description;
    private Boolean has_issues;
    private int open_issues;
    private String created_at;
    private OWNER owner;
    private class OWNER {
        public String login;
    }

    public String getUserName() {
        return owner.login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHas_issues(Boolean has_issues) {
        this.has_issues = has_issues;
    }

    public Boolean getHas_issues() {
        return has_issues;
    }

    public void setOpen_issues(int open_issues) {
        this.open_issues = open_issues;
    }

    public int getOpen_issues() {
        return open_issues;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
