package fr.kainovaii.spark.app.repository;

import fr.kainovaii.spark.app.models.Project;
import org.javalite.activejdbc.LazyList;

public class ProjectRepository
{
    public void create(String icon, String title, String description, String link, String tools)
    {
        Project project = new Project();
        project.set("icon", icon, "title", title, "description", description, "link", link, "tools", tools);
        project.saveIt();
    }

    public boolean updateById(int id, String newIcon, String newTitle, String newDescription, String newLink, String newTools)
    {
        Project project = this.findById(id);
        if (project == null) return false;
        project.set("icon", newIcon, "title", newTitle, "description", newDescription, "link", newLink, "tools", newTools);
        return project.saveIt();
    }

    public boolean deleteById(int id)
    {
        Project project = this.findById(id);
        if (project == null) return false;
        return project.delete();
    }

    public static boolean projectExist(int id) { return Project.findById(id) != null; }

    public LazyList<Project> getAll() { return Project.findAll(); }

    public Project findById(int id) { return Project.findById(id); }
}
