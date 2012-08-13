package com.vmware.vsh.app;

import org.junit.Test;

public class CommandTest {

    @SuppressWarnings("unused")
    private class TestCommand implements Command {

        @Override
        public void setName(String name) {
        }

        @Override
        public String getName() {
            return null;
        }

    }

}
