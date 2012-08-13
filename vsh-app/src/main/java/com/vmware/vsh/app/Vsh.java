package com.vmware.vsh.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import com.vmware.vsh.app.parser.CommandParser;
import com.vmware.vsh.app.parser.ParseException;

public class Vsh {

    private static final Logger LOG = Logger.getLogger(Vsh.class.getName());

    // TODO: handle exceptions
    public static void main(String[] args) throws IOException, ParseException {
        VshRC vshrc = new VshRC();
        if (!vshrc.getRCFile().exists()) {
            vshrc.initViaPrompt(new InputStreamReader(System.in),
                    new OutputStreamWriter(System.out));
        }
        vshrc.load();
        // TODO: don't hard code host config name
        HostConfig cfg = vshrc.getHostConfig("default");
        String url = "https://" + cfg.getAddress() + "/sdk";
        System.out.println("URL: " + url);
        String user = cfg.getLogin();
        String pass = cfg.getPassword();
        StringBuilder buf = new StringBuilder();
        String delim = "";
        for (String arg : args) {
            buf.append(delim);
            delim = " ";
            buf.append(arg);
        }
        buf.append("\n");
        CommandParser parser = new CommandParser(new StringReader(
                buf.toString()));
        List<Command> commands = parser.commandList();
        for (Command cmd : commands) {
            cmd.exec(url, user, pass);
        }
    }

}
