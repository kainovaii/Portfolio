package fr.kainovaii.spark.app.models;

import org.javalite.activejdbc.Model;

public class Skill extends Model
{
    public String getIcon() {
        return getString("icon");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getTools() {
        return getString("tools");
    }

    public void setIcon(String icon) {
        set("icon", icon);
    }

    public void setTitle(String title) {
        set("title", title);
    }

    public void setTools(String tools) {
        set("tools", tools);
    }
}
