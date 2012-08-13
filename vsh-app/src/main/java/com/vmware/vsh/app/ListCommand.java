package com.vmware.vsh.app;

import com.vmware.vsh.VSphereClient;

public class ListCommand extends CommandAdapter {

    @Override
    public void exec(String url, String user, String password) {
        new VSphereClient().connect(url, user, password);
    }
}
