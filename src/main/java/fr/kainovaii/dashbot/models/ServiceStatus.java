package fr.kainovaii.dashbot.models;

public class ServiceStatus
{
    private String serviceName;
    private String serviceUrl;
    private String statusMessage;
    private String time;
    private int ping;

    public ServiceStatus(String serviceName, String serviceUrl, String statusMessage, String time, int ping)
    {
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
        this.statusMessage = statusMessage;
        this.time = time;
        this.ping = ping;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceUrl() { return serviceUrl; }
    public void setServiceUrl(String serviceUrl) { this.serviceUrl = serviceUrl; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getPing() { return ping; }
    public void setPing(int ping) { this.ping = ping; }
}
