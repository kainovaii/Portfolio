package fr.kainovaii.porfolio.app.domain.user;

import fr.kainovaii.obsidian.database.DB;
import fr.kainovaii.obsidian.database.seeder.SeederInterface;
import fr.kainovaii.obsidian.database.seeder.annotations.Seeder;
import fr.kainovaii.obsidian.di.annotations.Inject;

@Seeder(priority = 10)
public class UserSeeder implements SeederInterface
{
    @Inject
    private UserRepository userRepository;

    @Override
    public void seed()
    {
        DB.withConnection(() -> {
            if (userRepository.findAll().isEmpty()) {
                userRepository.create("admin", "admin@local.dev", "$2a$12$7ZXd5t3DlUtcfCJGs52R7uDINIbqsGa5D82uFTmdXk5PEeJu3uZnu", "ADMIN");
            }
            return null;
        });
    }
}