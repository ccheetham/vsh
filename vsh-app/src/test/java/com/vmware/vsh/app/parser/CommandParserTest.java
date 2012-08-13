package com.vmware.vsh.app.parser;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.vmware.vsh.app.Command;
import com.vmware.vsh.app.ListCommand;

public class CommandParserTest {

    @Test
    public void testListCommand() throws ParseException {
        // TODO: get this path in portable manner 
        System.setProperty("com.vmware.vsh.app.commands.path",
                "/Users/ccheetham/workspace/vsh/vsh-app/src/main/java/com/vmware/vsh/app/command.properties");
        Reader in = new StringReader("list\n");
        CommandParser parser = new CommandParser(in);
        List<Command> commands = parser.commandList();
        assertEquals("command count mismatch", 1, commands.size());
        Command cmd = commands.get(0);
        assertEquals("command class mismatch", ListCommand.class, cmd.getClass());
    }
}
