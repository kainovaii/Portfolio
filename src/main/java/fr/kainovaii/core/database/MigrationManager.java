package fr.kainovaii.core.database;

import org.javalite.activejdbc.Base;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MigrationManager
{
    private final DB database;
    private final Logger logger;
    private final List<Migration> migrations;
    private final String dbType;

    public MigrationManager(DB database, Logger logger) {
        this.database = database;
        this.logger = logger;
        this.migrations = new ArrayList<>();
        this.dbType = database.getType();
    }

    public MigrationManager add(Migration migration) {
        migration.type = this.dbType;
        migration.logger = this.logger;
        migrations.add(migration);
        return this;
    }

    public void migrate() {
        database.withConnection(() -> {
            createMigrationsTable();

            for (int i = 0; i < migrations.size(); i++) {
                String migrationName = "migration_" + (i + 1);

                if (!isMigrationExecuted(migrationName)) {
                    logger.info("Exécution migration: " + migrationName);
                    migrations.get(i).up();
                    recordMigration(migrationName);
                    logger.info("✓ Migration complétée: " + migrationName);
                } else {
                    logger.fine("Migration déjà exécutée: " + migrationName);
                }
            }

            logger.info("Toutes les migrations sont à jour");
            return null;
        });
    }

    public void rollback() {
        database.withConnection(() -> {
            for (int i = migrations.size() - 1; i >= 0; i--) {
                String migrationName = "migration_" + (i + 1);

                if (isMigrationExecuted(migrationName)) {
                    logger.info("Annulation migration: " + migrationName);
                    migrations.get(i).down();
                    removeMigration(migrationName);
                    logger.info("✓ Migration annulée: " + migrationName);
                }
            }

            logger.info("Toutes les migrations ont été annulées");
            return null;
        });
    }

    public void rollbackLast() {
        database.withConnection(() -> {
            for (int i = migrations.size() - 1; i >= 0; i--) {
                String migrationName = "migration_" + (i + 1);

                if (isMigrationExecuted(migrationName)) {
                    logger.info("Annulation dernière migration: " + migrationName);
                    migrations.get(i).down();
                    removeMigration(migrationName);
                    logger.info("✓ Dernière migration annulée");
                    break;
                }
            }
            return null;
        });
    }

    public void fresh() {
        rollback();
        migrate();
    }

    public void status() {
        database.withConnection(() -> {
            System.out.println("\n=== Statut des migrations ===");
            for (int i = 0; i < migrations.size(); i++) {
                String migrationName = "migration_" + (i + 1);
                String status = isMigrationExecuted(migrationName) ? "✓ Exécutée" : "✗ En attente";
                System.out.println(migrationName + " - " + status);
            }
            System.out.println("=============================\n");
            return null;
        });
    }


    private void createMigrationsTable() {
        String idColumn = switch (dbType) {
            case "mysql" -> "INT AUTO_INCREMENT PRIMARY KEY";
            case "postgresql" -> "SERIAL PRIMARY KEY";
            default -> "INTEGER PRIMARY KEY AUTOINCREMENT";
        };

        Base.exec(String.format("""
            CREATE TABLE IF NOT EXISTS migrations (
                id %s,
                migration VARCHAR(255) NOT NULL,
                executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """, idColumn));
    }

    private boolean isMigrationExecuted(String migrationName) {
        Object result = Base.firstCell("SELECT COUNT(*) FROM migrations WHERE migration = ?", migrationName);
        if (result == null) return false;

        long count = result instanceof Long ? (Long) result : Long.parseLong(result.toString());
        return count > 0;
    }

    private void recordMigration(String migrationName) {
        Base.exec("INSERT INTO migrations (migration) VALUES (?)", migrationName);
    }

    private void removeMigration(String migrationName) {
        Base.exec("DELETE FROM migrations WHERE migration = ?", migrationName);
    }
}