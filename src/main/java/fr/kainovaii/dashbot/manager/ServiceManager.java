package fr.kainovaii.dashbot.manager;

import fr.kainovaii.dashbot.models.ServiceStatus;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ServiceManager
{
    public static final Map<String, ServiceStatus> statuses = new ConcurrentHashMap<>();

    public static void updateStatus(String serviceName, ServiceStatus status)
    {
        statuses.put(serviceName, status);
    }

    public static ServiceStatus getStatus(String serviceName)
    {
        return statuses.get(serviceName);
    }
}
