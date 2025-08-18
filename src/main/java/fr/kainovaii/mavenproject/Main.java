package fr.kainovaii.mavenproject;

import io.github.cdimascio.dotenv.Dotenv;
public class Main
{
    private static Dotenv dotenv;

    public static void main(String[] args) throws Exception
    {
        dotenv = loadEnv();

        System.out.println("Hello world");
    }

    private static Dotenv loadEnv() { return Dotenv.configure().directory("../").load(); }

    private static String getEnv(String key)
    {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("La variable d'environnement " + key + " est manquante !");
        }
        return value;
    }
}
