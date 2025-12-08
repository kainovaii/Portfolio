package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.spark.app.models.Project;
import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.app.repository.ProjectRepository;
import fr.kainovaii.spark.core.database.DB;
import fr.kainovaii.spark.core.web.controller.BaseController;
import fr.kainovaii.spark.core.web.controller.Controller;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class ProjectController extends BaseController
{
    private final ProjectRepository projectRepository;

    public ProjectController()
    {
        initRoutes();
        this.projectRepository = new ProjectRepository();
    }

    private void initRoutes()
    {
        get("/admin/projects", this::list);
        post("/api/projects/create", this::create);
        post("/api/projects/update", this::update);
        get("/api/projects/delete/:id", this::delete);
    }

    private Object list(Request req, Response res)
    {
        requireLogin(req, res);

        List<Project> projects = DB.withConnection(() -> projectRepository.getAll().stream().toList());

        return render("admin/projects/projects.html", Map.of("projects", projects));
    }

    private Object create(Request req, Response res)
    {
        requireLogin(req, res);

        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String description = req.queryParams("description");
        String link = req.queryParams("link");
        String[] tools = req.queryParamsValues("tools[]");

        boolean query = projectRepository.create(icon, title, description, link, Arrays.toString(tools));

        if (!query) redirectWithFlash(req, res, "error", "Creating error", "/admin/projects");

        redirectWithFlash(req, res, "success", "Creating success", "/admin/projects");
        return true;
    }

    private Object delete(Request req, Response res)
    {
        requireLogin(req, res);

        int skillId = Integer.parseInt(req.params("id"));

        boolean query = DB.withConnection(() -> projectRepository.deleteById(skillId));

        if (!query) redirectWithFlash(req, res, "error", "Delete error", "/admin/projects");

        redirectWithFlash(req, res, "success", "Delete success", "/admin/projects");
        return true;
    }

    private Object update(Request req, Response res)
    {
        requireLogin(req, res);

        int id = Integer.parseInt(req.queryParams("id"));
        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String description = req.queryParams("description");
        String link = req.queryParams("link");
        String[] tools = req.queryParamsValues("tools[]");

        boolean query = projectRepository.updateById(id, icon, title, description, link, Arrays.toString(tools));

        if (!query) redirectWithFlash(req, res, "error", "Update error", "/admin/projects");

        redirectWithFlash(req, res, "success", "Update success", "/admin/projects");
        return true;
    }
}
