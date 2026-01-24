package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.methods.POST;
import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.app.repository.SkillRepository;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class SkillController extends BaseController
{
    private final SkillRepository skillRepository = new SkillRepository();

    @HasRole("ADMIN")
    @GET("/admin/skills")
    private Object list(Request req, Response res)
    {
        List<Skill> skills = DB.withConnection(() -> skillRepository.getAll().stream().toList());
        return render("admin/skills/skills.html", Map.of("skills", skills));
    }

    @HasRole("ADMIN")
    @POST("/api/skills/create")
    private Object create(Request req, Response res)
    {
        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String[] tools = req.queryParamsValues("tools[]");
        boolean query = skillRepository.create(icon, title, Arrays.toString(tools));
        if (!query) redirectWithFlash(req, res, "error", "Creating error", "/admin/skills");
        redirectWithFlash(req, res, "success", "Creating success", "/admin/skills");
        return true;
    }

    @HasRole("ADMIN")
    @POST("/api/skills/update")
    private Object update(Request req, Response res)
    {
        int id = Integer.parseInt(req.queryParams("id"));
        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String[] tools = req.queryParamsValues("tools[]");
        boolean query = skillRepository.updateById(id, icon, title, Arrays.toString(tools));
        if (!query) redirectWithFlash(req, res, "error", "Update error", "/admin/skills");
        redirectWithFlash(req, res, "success", "Update success", "/admin/skills");
        return true;
    }

    @HasRole("ADMIN")
    @GET("/api/skills/delete/:id")
    private Object delete(Request req, Response res)
    {
        int skillId = Integer.parseInt(req.params("id"));
        boolean query = DB.withConnection(() -> skillRepository.deleteById(skillId));
        if (!query) redirectWithFlash(req, res, "error", "Delete error", "/admin/skills");
        redirectWithFlash(req, res, "success", "Delete success", "/admin/skills");
        return true;
    }
}