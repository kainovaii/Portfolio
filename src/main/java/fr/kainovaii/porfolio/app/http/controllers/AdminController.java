package fr.kainovaii.porfolio.app.http.controllers;

import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.routing.methods.GET;
import fr.kainovaii.obsidian.routing.methods.POST;
import fr.kainovaii.obsidian.security.role.HasRole;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

@Controller
public class AdminController extends BaseController
{
    @HasRole("ADMIN")
    @GET(value = "/admin", name = "admin.dashboard")
    private Object homepage(Request req, Response res) {
        return render("admin/dashboard.html", Map.of());
    }
}