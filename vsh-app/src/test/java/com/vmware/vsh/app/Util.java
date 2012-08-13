package com.vmware.vsh.app;

import java.io.File;
import java.io.IOException;

class Util {

    static File createTempFile(String prefix) throws IOException {
        File rc = File.createTempFile(prefix, null);
        rc.deleteOnExit();
        return rc;
    }


}
