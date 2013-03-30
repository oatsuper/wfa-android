package com.patilparagp.wfa.model;

public class ServerCredentials {
    private String server;
    private String userName;
    private String password;


    public ServerCredentials(String server, String userName, String password) {
        this.server = server;
        this.userName = userName;
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
