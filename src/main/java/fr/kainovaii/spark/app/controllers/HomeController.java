package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import fr.kainovaii.spark.app.models.Project;
import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.app.repository.ProjectRepository;
import fr.kainovaii.spark.app.repository.SkillRepository;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController extends BaseController
{
    private final SkillRepository skillRepository = new SkillRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();

    @GET(value = "/", name = "site_home")
    private Object homepage(Request req, Response res)
    {
        List<Skill> skills = DB.withConnection(() -> skillRepository.getAll().stream().toList());
        return render("landing/home.html", Map.of("skills", skills));
    }

    @GET(value = "/projects", name = "site_projects")
    private Object projects(Request req, Response res)
    {
        List<Project> projects = DB.withConnection(() -> projectRepository.getAll().stream().toList());
        return render("landing/projects.html", Map.of("projects", projects));
    }
}