package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.spark.app.models.Project;
import fr.kainovaii.spark.app.models.Skill;
import fr.kainovaii.spark.app.repository.ProjectRepository;
import fr.kainovaii.spark.app.repository.SkillRepository;
import fr.kainovaii.spark.core.database.DB;
import fr.kainovaii.spark.core.web.controller.BaseController;
import spark.Request;
import spark.Session;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GlobalAdviceController extends BaseController
{
    record User(String username, String role) {}

    public static void applyGlobals(Request req)
    {
        Session session = req.session(true);
        SkillRepository skillRepository = new SkillRepository();
        ProjectRepository projectRepository = new ProjectRepository();

        String username = Optional.ofNullable(session.attribute("username")).orElse("Invité").toString();
        String role = Optional.ofNullable(session.attribute("role")).orElse("").toString();
        List<User> loggedUser = Collections.singletonList(new User(username, role));

        List<Skill> skills = DB.withConnection(() -> skillRepository.getAll().stream().toList());
        List<Project> projects = DB.withConnection(() -> projectRepository.getAll().stream().toList());

        setGlobal("title", "The Guardian");
        setGlobal("isLogged", isLogged(req));
        setGlobal("loggedUser", loggedUser);

        setGlobal("skills", skills);
        setGlobal("projects", projects);

        Map<String, String> flashes = collectFlashes(req);
        setGlobal("flashes", flashes);
    }
}