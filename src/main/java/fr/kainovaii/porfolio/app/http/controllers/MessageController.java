package fr.kainovaii.porfolio.app.http.controllers;

import fr.kainovaii.obsidian.database.DB;
import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.routing.methods.GET;
import fr.kainovaii.obsidian.routing.methods.POST;
import fr.kainovaii.obsidian.security.csrf.annotations.CsrfProtect;
import fr.kainovaii.obsidian.security.role.HasRole;
import fr.kainovaii.obsidian.validation.RequestValidator;
import fr.kainovaii.obsidian.validation.ValidationResult;
import fr.kainovaii.porfolio.app.domain.message.Message;
import fr.kainovaii.porfolio.app.domain.message.MessageRepository;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

@Controller
public class MessageController extends BaseController
{
    @HasRole("ADMIN")
    @GET(value = "/admin/messages", name = "admin.message.list")
    private Object list(MessageRepository messageRepository)
    {
        List<Message> messages = DB.withConnection(() -> messageRepository.findAllSortedByDate().stream().toList());
        return render("admin/message/list.html", Map.of("messages", messages));
    }

    @CsrfProtect
    @POST(value = "/message", name = "admin.message.create")
    private Object create(Request req, Response res, MessageRepository messageRepository)
    {
        ValidationResult result = RequestValidator.validateSafe(req, Map.of(
            "name", "required|min:5",
            "email", "required|email|min:5",
            "message", "required|min:100"
        ));

        if (result.fails()) {
            return render("home.html", Map.of(
                "errors", result.getErrors(),
                "old", req.queryMap().toMap()
            ));
        }

        if (result.isValid()) {
            boolean createMessage = DB.withConnection(() -> messageRepository.create(
                req.queryParams("name"),
                req.queryParams("email"),
                req.queryParams("message")
            ));

            if (createMessage) return redirectWithFlash(req, res, "info","Le message a bien été envoyé, vous recevrez une réponse dans les meilleurs délais", "/");
        }

        return "";
    }

    @CsrfProtect
    @POST(value = "/message/delete", name = "admin.message.delete")
    private Object delete(Request req, Response res, MessageRepository messageRepository)
    {
        boolean query = DB.withConnection(() -> messageRepository.delete(Integer.parseInt(req.queryParams("id"))));
        if (query) return redirectWithFlash(req, res, "info", "Successfully deleting", "/admin/messages");
        return redirectWithFlash(req, res, "error", "Unsuccessfully deleting", "/admin/messages");
    }
}