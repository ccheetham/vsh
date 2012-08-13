package com.vmware.vsh.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommandAdapterTest {

    @Test
    public void testName() {
        CommandAdapter cmd = new CommandAdapter() {};
        cmd.setName("foo");
        assertEquals("command name mismatch", "foo", cmd.getName());
    }

}
