package fr.kainovaii.spark.core.web.controller;

import fr.kainovaii.spark.core.web.template.TemplateManager;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.halt;

public class BaseController
{
    protected static boolean isLogged(Request req)
    {
        Session session = req.session(false);
        if (session == null) return false;

        Boolean logged = session.attribute("logged");
        return Boolean.TRUE.equals(logged);
    }

    protected void requireLogin(Request req, Response res)
    {
        if (!isLogged(req)) {
            res.redirect("/login");
            halt();
        }
    }

    protected void setFlash(Request req, String key, String message)
    {
        Session session = req.session();
        session.attribute("flash_" + key, message);
    }

    protected String render(String template, Map<String, Object> model)
    {
        ModelAndView mv = new ModelAndView(model, template);
        return TemplateManager.get().render(mv);
    }

    protected String render(String template)
    {
        return render(template, Map.of());
    }

    public static Map<String, String> collectFlashes(Request req)
    {
        Session session = req.session(false);
        if (session == null) return Map.of();

        Map<String, String> flashes = new HashMap<>();
        for (String attr : session.attributes()) {
            if (attr.startsWith("flash_")) {
                String key = attr.substring(6);
                flashes.put(key, session.attribute(attr));
                session.removeAttribute(attr);
            }
        }
        return flashes;
    }

    protected void redirectWithFlash(Request req, Response res, String type, String message, String location)
    {
        setFlash(req, type, message);
        res.redirect(location);
    }
}