package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.app.repository.SkillRepository;
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
public class SkillController extends BaseController
{
    private final SkillRepository skillRepository;

    public SkillController()
    {
        initRoutes();
        this.skillRepository = new SkillRepository();
    }

    private void initRoutes()
    {
        get("/admin/skills", this::list);
        post("/api/skills/create", this::create);
        post("/api/skills/update", this::update);
        get("/api/skills/delete/:id", this::delete);
    }

    private Object list(Request req, Response res)
    {
        requireLogin(req, res);

        List<Skill> skills = DB.withConnection(() -> skillRepository.getAll().stream().toList());

        return render("admin/skills/skills.html", Map.of("skills", skills));
    }

    private Object create(Request req, Response res)
    {
        requireLogin(req, res);

        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String[] tools = req.queryParamsValues("tools[]");

        boolean query = skillRepository.create(icon, title, Arrays.toString(tools));
        if (!query) redirectWithFlash(req, res, "error", "Creating error", "/admin/skills");

        redirectWithFlash(req, res, "success", "Creating success", "/admin/skills");
        return true;
    }

    private Object delete(Request req, Response res)
    {
        requireLogin(req, res);

        int skillId = Integer.parseInt(req.params("id"));

        boolean query = DB.withConnection(() -> skillRepository.deleteById(skillId));

        if (!query) redirectWithFlash(req, res, "error", "Delete error", "/admin/skills");

        redirectWithFlash(req, res, "success", "Delete success", "/admin/skills");
        return true;
    }

    private Object update(Request req, Response res)
    {
        requireLogin(req, res);

        int id = Integer.parseInt(req.queryParams("id"));
        String icon = req.queryParams("icon");
        String title = req.queryParams("title");
        String[] tools = req.queryParamsValues("tools[]");

        boolean query = skillRepository.updateById(id, icon, title, Arrays.toString(tools));
        if (!query) redirectWithFlash(req, res, "error", "Update error", "/admin/skills");

        redirectWithFlash(req, res, "success", "Update success", "/admin/skills");
        return true;
    }
}