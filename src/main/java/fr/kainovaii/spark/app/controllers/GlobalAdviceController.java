package fr.kainovaii.spark.app.controllers;

import fr.kainovaii.spark.core.web.controller.BaseController;
import spark.Request;
import spark.Session;

public class GlobalAdviceController extends BaseController
{
    record User(String username, String role) {}

    public static void applyGlobals(Request req)
    {
        Session session = req.session(true);
    }
}