package com.vmware.vsh.app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * <code>CommandLoader</code> ... loads the command implementation based on a
 * property file configuration.
 * 
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class CommandLoader {

    public static final String PROPERTY_PATH = "com.vmware.vsh.app.commands.path";

    public static final String NAMES = "com.vmware.vsh.app.commands";

    private static final Logger LOG = Logger.getLogger(CommandLoader.class
            .getName());

    private static final String CLASS_NAME = "com.vmware.vsh.app.command.%s.class";

    public Map<String, Command> loadCommands() throws CommandLoaderException {
        Map<String, Command> commands = new HashMap<String, Command>();
        String cmdsPath = System.getProperty(PROPERTY_PATH);
        LOG.fine("loading commands definitions from " + cmdsPath);
        // TODO: unit test for load form classpath?
        Reader in;
        if (cmdsPath != null) {
            try {
                in = new FileReader(cmdsPath);
            } catch (FileNotFoundException ex) {
                // TODO: why is this being swallowed in command parsr test?
                throw new CommandLoaderException("command properties not found: "
                        + cmdsPath, ex);
            }
        } else {
            in = new InputStreamReader(getClass().getResourceAsStream("/com/vmware/vsh/app/command.properties"));
        }
        Properties cmdProps = new Properties();
        try {
            cmdProps.load(in);
        } catch (IOException ex) {
            throw new CommandLoaderException(
                    "error reading command properties " + cmdsPath + ": "
                            + ex.getLocalizedMessage(), ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                LOG.warning("failed to close command properties input stream, continuing anyway: "
                        + ex.getLocalizedMessage());
            }
        }
        String[] names = cmdProps.getProperty(NAMES).split(",");
        for (String name : names) {
            LOG.fine("loading command '" + name + "'");
            String clazzName = cmdProps.getProperty(String.format(CLASS_NAME,
                    name));
            try {
                Class<? extends Command> clazz = Class.forName(clazzName)
                        .asSubclass(Command.class);
                Command cmd = clazz.newInstance();
                commands.put(name, cmd);
            } catch (ClassNotFoundException ex) {
                throw new CommandLoaderException(
                        "unable to find class for command '" + name + "':"
                                + clazzName, ex);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return commands;
    }

    public static Command forName(String name) {
        // TODO: don't swallow exception
        try {
            return new CommandLoader().loadCommands().get(name);
        } catch (CommandLoaderException ex) {
            return null;
        }
    }

}
