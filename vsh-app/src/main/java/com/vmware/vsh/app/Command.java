package com.vmware.vsh.app;


public interface Command {

    void setName(String name);

    String getName();

    // TODO: exec should have no opts?
    void exec(String url, String user, String passwword);

}
