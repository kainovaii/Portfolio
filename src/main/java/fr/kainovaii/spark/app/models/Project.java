package fr.kainovaii.spark.app.models;

import org.javalite.activejdbc.Model;

public class Project extends Model
{
    public String getIcon() {
        return getString("icon");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getDescription() {
        return getString("description");
    }

    public String getLink() {
        return getString("link");
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

    public void setDescription(String description) {
        set("description", description);
    }

    public void setLink(String link) {
        set("link", link);
    }

    public void setTools(String tools) {
        set("tools", tools);
    }
}
