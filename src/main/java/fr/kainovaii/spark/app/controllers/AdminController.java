package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.spark.app.repository.ProjectRepository;
import fr.kainovaii.spark.app.repository.SkillRepository;
import spark.Request;
import spark.Response;

import java.util.Map;

import static spark.Spark.get;

@Controller
public class AdminController extends BaseController
{
    private final SkillRepository skillRepository = new SkillRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();

    @HasRole("ADMIN")
    @GET(value = "/admin", name = "admin_home")
    private Object list(Request req, Response res)
    {
        int skills_count = DB.withConnection(() -> skillRepository.getAll().size());
        int projects_count = DB.withConnection(() -> projectRepository.getAll().size());

        return render("admin/dashboard.html", Map.of(
            "skills_count", skills_count,
            "projects_count", projects_count
        ));
    }
}
