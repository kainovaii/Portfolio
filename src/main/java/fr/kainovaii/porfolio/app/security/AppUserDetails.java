package fr.kainovaii.porfolio.app.security;

import fr.kainovaii.obsidian.security.user.UserDetails;

public interface AppUserDetails extends UserDetails
{
    String getEmail();
}
