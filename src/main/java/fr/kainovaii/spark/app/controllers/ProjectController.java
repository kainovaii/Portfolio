package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.spark.core.web.controller.BaseController;
import fr.kainovaii.spark.core.web.controller.Controller;
import spark.Request;
import spark.Response;

import java.util.Map;

import static spark.Spark.get;

@Controller
public class ProjectController extends BaseController
{
    public ProjectController() { initRoutes(); }

    private void initRoutes()
    {
        get("/admin", this::dashboard);
    }

    private Object dashboard(Request req, Response res)
    {
        requireLogin(req, res);
        return render("admin/dashboard.html", Map.of());
    }
}
