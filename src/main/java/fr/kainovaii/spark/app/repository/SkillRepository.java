package fr.kainovaii.spark.app.repository;

import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.core.database.DB;
import org.javalite.activejdbc.LazyList;

public class SkillRepository
{
    public boolean create(String icon, String title, String tools)
    {
        return DB.withConnection(() -> {
            Skill skill = new Skill();
            skill.set("icon", icon, "title", title, "tools", tools);
            return skill.saveIt();
        });
    }

    public boolean updateById(int id, String newIcon, String newTitle, String newTools)
    {
        return DB.withConnection(() -> {
            Skill skill = this.findById(id);
            if (skill == null) return false;
            skill.set("icon", newIcon, "title", newTitle, "tools", newTools);
            return skill.saveIt();
        });
    }

    public boolean deleteById(int id)
    {
        Skill skill = this.findById(id);
        if (skill == null) return false;
        return skill.delete();
    }

    public static boolean skillExist(int id) { return Skill.findById(id) != null; }

    public LazyList<Skill> getAll() { return Skill.findAll(); }

    public Skill findById(int id) { return Skill.findById(id); }
}
