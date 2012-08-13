package com.vmware.vsh.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <code>VshRC</code> provides an abstraction to the configuration of the
 * <tt>vsh</tt> application. When <tt>vsh</tt> needs to access a remote service,
 * it obtains the information needed from an instance of a <code>VshRC</code>.
 * <code>VshRC</code> obtains its host configuration information from a
 * configuration file.
 * <p>
 * The <code>VshRC</code> configuration file is made up of lines of the format
 * <em>keyword arguments</em>. The <em>keywords</em> are:
 * <dl>
 * <dt><tt>Host</tt></dt>
 * <dd>Begins the definition of a host configuration. The configuration ends at
 * the next <tt>Host</tt> configuration or end of the file.</dd>
 * <dt><tt>User</tt></dt>
 * <dd>The user to login as.</dd>
 * <dt><tt>Password</tt></dt>
 * <dd>The user's password.</dd>
 * <dt><tt>Address</tt></dt>
 * <dd>The host address to be used to access the remote service. If not
 * supplied, the <tt>Host</tt> value is used. Useful for using aliases/nicknames
 * when accessing hosts.</dd>
 * </dl>
 * 
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class VshRC {

    /** Default vshrc path ($HOME/.vshrc). */
    public static final File DEFAULT_FILE = new File(
            System.getProperty("user.home"), ".vshrc");

    private static final Logger LOG = Logger.getLogger(VshRC.class.getName());

    private final File vshrc;

    private Map<String, HostConfig> configs = new HashMap<String, HostConfig>();

    /**
     * Create a new <code>VshRC</code> using the default vshrc file location.
     * The default file location is the file named <tt>.vshrc</tt> in the user's
     * home directory.
     */
    public VshRC() {
        this(DEFAULT_FILE);
    }

    /**
     * Create a new <code>VshRC</code> using the specified file as the backing
     * store.
     * 
     * @param config
     *            vshrc configuration file
     */
    public VshRC(File vshrc) {
        this.vshrc = vshrc;
    }

    /**
     * Return the rc (configuration) file supporting this instance.
     * 
     * @return supporting rc file
     */
    public File getRCFile() {
        return vshrc;
    }

    public void addHostConfig(HostConfig config) {
        configs.put(config.getName(), config);
    }

    /**
     * Return the specified host configuration.
     * 
     * @param name
     *            host name
     * @return specified host configuration
     */
    public HostConfig getHostConfig(String name) {
        return configs.get(name);
    }

    /**
     * Load the vshrc configuration, replacing any existing state.
     * 
     * @throws IOException
     */
    public void load() throws IOException {
        LOG.fine("loading vshrc from " + getRCFile());
        BufferedReader in = new BufferedReader(new FileReader(getRCFile()));
        configs.clear();
        try {
            String line;
            HostConfig cfg = null;
            while ((line = in.readLine()) != null) {
                String[] fields = line.split("\\s+", 2);
                String keyword = fields[0];
                String argument = fields[1];
                if ("Host".equalsIgnoreCase(keyword)) {
                    cfg = new HostConfig(argument);
                    configs.put(cfg.getName(), cfg);
                    LOG.finer("loading vshrc host config " + cfg.getName());
                } else if ("Address".equalsIgnoreCase(keyword)) {
                    cfg.setAddress(argument);
                } else if ("Login".equalsIgnoreCase(keyword)) {
                    cfg.setLogin(argument);
                } else if ("Password".equalsIgnoreCase(keyword)) {
                    cfg.setPassword(argument);
                }
            }
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                LOG.warning("failed to close vshrc in stream: " + ex);
            }
        }
    }

    public void store() throws IOException {
        LOG.fine("storing vshrc to " + getRCFile());
        PrintWriter out = new PrintWriter(getRCFile());
        String delim = "";
        try {
            for (HostConfig cfg : configs.values()) {
                out.print(delim);
                writeNVP(out, "Host", cfg.getName());
                writeNVP(out, "Login", cfg.getLogin());
                writeNVP(out, "Password", cfg.getPassword());
                writeNVP(out, "Address", cfg.getAddress());
                delim = System.getProperty("line.separator");
                LOG.finer("stored vshrc host config " + cfg.getName());
            }
        } finally {
            out.close();
        }
    }

    public void initViaPrompt(Reader in, Writer out) throws IOException {
        BufferedReader _in = in instanceof BufferedReader ? (BufferedReader) in
                : new BufferedReader(in);
        PrintWriter _out = out instanceof PrintWriter ? (PrintWriter) out
                : new PrintWriter(out);
        _out.println();
        _out.println("Oops, your default vshrc file does not exist.");
        _out.println();
        _out.println("You'll be prompted for several values from which I'll");
        _out.println("create a vshrc file for you.  If you make a mistake,");
        _out.println("there'll be an opportunity at the end to correct.  You");
        _out.println("can hit [CTRL]-[C] at any time to quit.");
        _out.println();
        _out.println("Hit [ENTER] to continue... ");
        _out.flush();
        _in.readLine();
        _out.println();
        try {
            String host = null;
            String login = null;
            String password = null;
            String address = null;
            while (true) {
                final String newline = System.getProperty("line.separator");
                host = prompt(
                        _in,
                        _out,
                        "Host",
                        "This is the name used to reference a host configuration."
                                + newline
                                + "It can be a nickname if in subsequent steps you provide an address."
                                + newline
                                + "Otherwise, this must be a resolvable name.",
                        host, true);

                login = prompt(
                        _in,
                        _out,
                        "Login",
                        "This is the login credential used to access the host.",
                        login, true);

                password = prompt(
                        _in,
                        _out,
                        "Password",
                        "This is the password credential used to access the host.",
                        password, true);

                address = prompt(_in, _out, "Address",
                        "This is the address used to access the host.  If not supplied,"
                                + newline + "the value of Host will be used.",
                        address, false);

                _out.println("Host Configuration:");
                _out.println();
                _out.println("  Host     : " + host);
                _out.println("  Loging   : " + login);
                _out.println("  Password : " + password);
                _out.println("  Address  : "
                        + (address == null ? "" : address));
                _out.println();
                _out.print("Proceed? [Y/n]");
                _out.flush();
                String response = _in.readLine();
                if (response.length() == 0 || response.equalsIgnoreCase("y")) {
                    break;
                }
            }
            HostConfig cfg = new HostConfig(host);
            cfg.setLogin(login);
            cfg.setPassword(password);
            cfg.setAddress(address);
            addHostConfig(cfg);
            store();
            load();
        } finally {
            try {
                _in.close();
            } catch (IOException ex) {
                // OK
            }
            _out.close();
        }
    }

    private static final String prompt(BufferedReader in, PrintWriter out,
            String name, String description, String value, boolean required)
            throws IOException {
        out.println();
        out.println(description);
        out.println();
        String warning = "";
        while (true) {
            out.println(warning);
            out.print(name);
            out.print(" (");
            out.print(required ? "reqd" : "opt");
            if (value != null) {
                out.print(" ) (");
                out.print(value);
            }
            out.print(" ): ");
            out.flush();
            String response = in.readLine();
            if (response.length() > 0) {
                return response;
            }
            if (value != null) {
                return value;
            }
            if (!required) {
                return null;
            }
            warning = name + " is required.";
        }
    }

    private static final void writeNVP(PrintWriter out, String name,
            String value) {
        final String format = "%-10s %s%n";
        if (value != null) {
            out.printf(format, name, value);
        }
    }

}
