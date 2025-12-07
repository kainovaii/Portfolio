package fr.kainovaii.spark.core.web.template;

public class TemplateManager {

    private static final PebbleTemplateEngine engine = new PebbleTemplateEngine();

    public static PebbleTemplateEngine get() {
        return engine;
    }
}