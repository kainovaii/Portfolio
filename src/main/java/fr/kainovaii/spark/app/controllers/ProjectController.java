package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.methods.POST;
import fr.kainovaii.spark.app.models.Project;
import fr.kainovaii.spark.app.repository.ProjectRepository;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController extends BaseController
{
    private final ProjectRepository projectRepository = new ProjectRepository();

    @HasRole("ADMIN")
    @GET("/admin/projects")
    private Object list(Request req, Response res)
    {
        List<Project> projects = DB.withConnection(() -> projectRepository.getAll().stream().toList());
        return render("admin/projects/projects.html", Map.of("projects", projects));
    }

    @HasRole("ADMIN")
    @POST("/api/projects/create")
    private Object create(Request req, Response res)
    {
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

    @HasRole("ADMIN")
    @POST("/api/projects/update")
    private Object update(Request req, Response res)
    {
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

    @HasRole("ADMIN")
    @GET("/api/projects/delete/:id")
    private Object delete(Request req, Response res)
    {
        int skillId = Integer.parseInt(req.params("id"));

        boolean query = DB.withConnection(() -> projectRepository.deleteById(skillId));

        if (!query) redirectWithFlash(req, res, "error", "Delete error", "/admin/projects");

        redirectWithFlash(req, res, "success", "Delete success", "/admin/projects");
        return true;
    }
}
