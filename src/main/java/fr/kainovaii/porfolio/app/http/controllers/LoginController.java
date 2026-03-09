package fr.kainovaii.porfolio.app.http.controllers;

import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.routing.methods.GET;
import fr.kainovaii.obsidian.routing.methods.POST;
import fr.kainovaii.obsidian.security.csrf.annotations.CsrfProtect;
import fr.kainovaii.obsidian.security.role.HasRole;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Map;

@Controller
public class LoginController extends BaseController
{
    @GET("/users/login")
    private Object login(Request req, Response res) { return render("user/login.html", Map.of()); }

    @CsrfProtect
    @POST("/users/login")
    private Object loginBack(Request req, Response res)
    {
        String username = req.queryParams("username");
        String password = req.queryParams("password");
        Session session = req.session(true);

        if (login(username, password, session)) {
            return redirectWithFlash(req, res, "success","Welcome back", "/admin");
        }

        return redirectWithFlash(req, res, "error","Incorrect login", "/users/login");
    }

    @HasRole("DEFAULT")
    @GET("/users/logout")
    private Object logout(Request req, Response res)
    {
        logout(req.session(true));
        return redirectWithFlash(req, res, "success","Logged out", "/users/login");
    }
}