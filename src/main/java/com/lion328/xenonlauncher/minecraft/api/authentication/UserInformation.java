package com.lion328.xenonlauncher.minecraft.api.authentication;

public class UserInformation
{

    private final String id;
    private final String username;
    private final String accessToken;
    private final String clientToken;

    public UserInformation(String id, String username, String accessToken, String clientToken)
    {
        this.id = id;
        this.username = username;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public String getID()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getClientToken()
    {
        return clientToken;
    }
}
