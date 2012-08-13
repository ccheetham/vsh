package com.vmware.vsh.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CommandLoaderTest {

    @Test
    public void testLoad() throws CommandLoaderException, IOException {
        File propfile = Util.createTempFile("cmdload-");
        System.setProperty("com.vmware.vsh.app.commands.path",
                propfile.getAbsolutePath());
        PrintWriter out = new PrintWriter(new FileWriter(propfile));
        out.println("com.vmware.vsh.app.commands=foo,bar");
        out.println("com.vmware.vsh.app.command.foo.class="
                + FooCommand.class.getName());
        out.println("com.vmware.vsh.app.command.bar.class="
                + BarCommand.class.getName());
        out.close();
        CommandLoader loader = new CommandLoader();
        Map<String, Command> commands = loader.loadCommands();
        @SuppressWarnings("serial")
        List<String> names = new ArrayList<String>() {
            {
                add("foo");
                add("bar");
            }
        };
        assertEquals("commands count mismatch", names.size(), commands.size());
        for (String name : commands.keySet()) {
            assertTrue("command not loaded", names.remove(name));
        }
        assertTrue(commands.get("foo") instanceof FooCommand);
        assertTrue(commands.get("bar") instanceof BarCommand);
    }

    @Test
    public void testForName() throws IOException, CommandLoaderException {
        File propfile = Util.createTempFile("cmdload-");
        System.setProperty("com.vmware.vsh.app.commands.path",
                propfile.getAbsolutePath());
        PrintWriter out = new PrintWriter(new FileWriter(propfile));
        out.println("com.vmware.vsh.app.commands=foo");
        out.println("com.vmware.vsh.app.command.foo.class="
                + FooCommand.class.getName());
        out.close();
        assertEquals("class mismatch", FooCommand.class,
                CommandLoader.forName("foo").getClass());
    }

    public static class FooCommand extends CommandAdapter {

        @Override
        public void exec() {
            // TODO Auto-generated method stub
            
        }
    }

    public static class BarCommand extends CommandAdapter {

        @Override
        public void exec() {
            // TODO Auto-generated method stub
            
        }
    }

}
