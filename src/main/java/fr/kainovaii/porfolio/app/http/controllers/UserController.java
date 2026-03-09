package fr.kainovaii.porfolio.app.http.controllers;

import fr.kainovaii.obsidian.database.DB;
import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.http.middleware.annotations.Before;
import fr.kainovaii.obsidian.livecomponents.session.SessionMiddleware;
import fr.kainovaii.obsidian.routing.methods.GET;
import fr.kainovaii.obsidian.routing.methods.POST;
import fr.kainovaii.obsidian.security.csrf.annotations.CsrfProtect;
import fr.kainovaii.obsidian.security.role.HasRole;
import fr.kainovaii.obsidian.validation.RequestValidator;
import fr.kainovaii.obsidian.validation.ValidationResult;
import fr.kainovaii.porfolio.app.domain.message.Message;
import fr.kainovaii.porfolio.app.domain.message.MessageRepository;
import fr.kainovaii.porfolio.app.domain.user.User;
import fr.kainovaii.porfolio.app.domain.user.UserRepository;
import fr.kainovaii.porfolio.app.security.AppUserDetails;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController
{
    @Before(SessionMiddleware.class)
    @HasRole("ADMIN")
    @GET(value = "/admin/users", name = "admin.users.list")
    private Object list(UserRepository userRepository)
    {
        List<User> users = DB.withConnection(() -> userRepository.findAll().stream().toList());
        return render("admin/user/list.html", Map.of("users", users));
    }

    @CsrfProtect
    @HasRole("DEFAULT")
    @POST(value = "/users/create", name = "user.update")
    private Object create(Request req, Response res, UserRepository userRepository)
    {
        List<User> users = DB.withConnection(() -> userRepository.findAll().stream().toList());

        ValidationResult result = RequestValidator.validateSafe(req, Map.of(
                "username", "required|min:5",
                "email", "email|required|min:5",
                "password", "required|min:8"
        ));

        if (result.fails()) {
            return render("admin/user/list.html", Map.of(
                    "errors", result.getErrors(),
                    "old", req.queryMap().toMap(),
                    "users", users,
                    "ocRestore", "create-user"
            ));
        }

        if (result.isValid()) {
            DB.withConnection(() -> {
                return userRepository.create(
                    req.queryParams("username"),
                    req.queryParams("email"),
                    hashPassword(req.queryParams("password")),
                    req.queryParams("role")
                );
            });
            return redirectWithFlash(req, res, "success","Update successfully", "/admin/users");
        }
        return redirectWithFlash(req, res, "info","You have been redirected", "/");
    }

    @CsrfProtect
    @HasRole("DEFAULT")
    @POST(value = "/users/update", name = "user.update")
    private Object update(Request req, Response res, UserRepository userRepository)
    {
        int userid = Integer.parseInt(req.queryParams("id"));

        List<User> users = DB.withConnection(() -> userRepository.findAll().stream().toList());
        User user = DB.withConnection(() -> userRepository.findById(userid));

        ValidationResult result = RequestValidator.validateSafe(req, Map.of(
            "username", "required|min:5",
            "email", "email|required|min:5",
            "password", "min:8"
        ));

        if (result.fails()) {
            return render("admin/user/list.html", Map.of(
                "errors", result.getErrors(),
                "old", req.queryMap().toMap(),
                "users", users,
                "ocRestore", "edit-user-" + userid
            ));
        }

        if (result.isValid()) {
            DB.withConnection(() -> {
                return userRepository.update(
                    user.getUsername(),
                    req.queryParams("username"),
                    req.queryParams("email"),
                    req.queryParams("password") != null ? hashPassword(req.queryParams("password")) : user.getPassword(),
                    req.queryParams("role")
                );
            });
            return redirectWithFlash(req, res, "success","Update successfully", "/admin/users");
        }
        return redirectWithFlash(req, res, "info","You have been redirected", "/");
    }

    @CsrfProtect
    @HasRole("DEFAULT")
    @POST(value = "/users/update/password", name = "user.update.password")
    private Object updatePassword(Request req, Response res, UserRepository userRepository)
    {
        AppUserDetails user = getLoggedUser(req);

        ValidationResult result = RequestValidator.validateSafe(req, Map.of(
                "password", "required|min:8",
                "password_confirm", "required|min:8"
        ));

        if (result.fails()) {
            return render("user/settings.html", Map.of(
                    "errors", result.getErrors(),
                    "old", req.queryMap().toMap()
            ));
        }

        if (result.isValid()) {
            DB.withConnection(() -> {
                return userRepository.update(
                    user.getUsername(),
                    user.getUsername(),
                    user.getEmail(),
                    hashPassword(req.queryParams("password")),
                    user.getRole()
                );
            });
            return redirectWithFlash(req, res, "success","Update successfully", "/users/settings");
        }
        return redirectWithFlash(req, res, "info","You have been redirected", "/");
    }

    @CsrfProtect
    @HasRole("DEFAULT")
    @POST(value = "/users/delete")
    public Object delete(Request req, Response res, UserRepository userRepository)
    {
        boolean query = DB.withConnection(() -> userRepository.delete(req.queryParams("username")));
        if (query) return redirectWithFlash(req, res, "success","Deleting successfully", "/admin/users");
        return redirectWithFlash(req, res, "info","You have been redirected", "/");
    }
}