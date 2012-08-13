package com.vmware.vsh.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

public class VshRCTest {

    private final String NEWLINE = System.getProperty("line.separator");

    private final String MY_HOST = "Host myhost" + NEWLINE
            + "Address myaddress" + NEWLINE + "Login mylogin" + NEWLINE
            + "Password mypass" + NEWLINE;

    private final String YOUR_HOST = "Host yourhost" + NEWLINE
            + "Address youraddress" + NEWLINE + "Login yourlogin" + NEWLINE
            + "Password yourpass" + NEWLINE;

    private final String DUP_HOST = "Host myhost" + NEWLINE
            + "Address dupaddress" + NEWLINE + "Login duplogin" + NEWLINE
            + "Password duppass" + NEWLINE;

    private final String ADDR_HOST = "Host addrhost" + NEWLINE
            + "Login addrlogin" + NEWLINE + "Password addrpass" + NEWLINE;

    @Test
    public void testDefaultRCLocation() {
        File expected = new File(System.getProperty("user.home"), ".vshrc");
        File actual = new VshRC().getRCFile();
        assertEquals("default vshrc location amiss", expected, actual);
    }

    @Test
    public void testRCLocation() {
        File expected = new File("/vshrc");
        File actual = new VshRC(expected).getRCFile();
        assertEquals("vshrc location amiss", expected, actual);
    }

    @Test
    public void testLoad() throws IOException {
        File tmprc = createRCFile(MY_HOST);
        VshRC vshrc = new VshRC(tmprc);
        vshrc.addHostConfig(new HostConfig("dummy"));
        assertTrue(vshrc.getHostConfig("myhost") == null);
        assertTrue(vshrc.getHostConfig("dummy") != null);
        vshrc.load();
        assertTrue(vshrc.getHostConfig("myhost") != null);
        assertTrue(vshrc.getHostConfig("dummy") == null);
    }

    @Test
    public void testStore() throws IOException {
        File tmprc = Util.createTempFile("vshrc-");
        VshRC vshrc = new VshRC(tmprc);
        HostConfig expected = new HostConfig("testhost");
        expected.setLogin("mylogin");
        expected.setPassword("mypass");
        expected.setAddress("myaddr");
        vshrc.addHostConfig(expected);
        vshrc.store();
        vshrc = new VshRC(tmprc);
        vshrc.load();
        HostConfig actual = vshrc.getHostConfig("testhost");
        assertTrue("stored config not found", actual != null);
        assertEquals("config mismatch", expected, actual);
    }

    @Test
    public void testSingleConfig() throws IOException {
        VshRC rc = createAndLoadVshRC(MY_HOST);
        HostConfig config = rc.getHostConfig("myhost");
        assertEquals("name mismatch", "myhost", config.getName());
        assertEquals("hostname mismatch", "myaddress", config.getAddress());
        assertEquals("login mismatch", "mylogin", config.getLogin());
        assertEquals("password mismatch", "mypass", config.getPassword());
    }

    @Test
    public void testMultipleConfig() throws IOException {
        VshRC rc = createAndLoadVshRC(MY_HOST + YOUR_HOST);
        HostConfig config = rc.getHostConfig("yourhost");
        assertEquals("name mismatch", "yourhost", config.getName());
        assertEquals("hostname mismatch", "youraddress", config.getAddress());
        assertEquals("login mismatch", "yourlogin", config.getLogin());
        assertEquals("password mismatch", "yourpass", config.getPassword());
    }

    @Test
    public void testLastOneWins() throws IOException {
        VshRC rc = createAndLoadVshRC(MY_HOST + DUP_HOST);
        HostConfig config = rc.getHostConfig("myhost");
        assertEquals("name mismatch", "myhost", config.getName());
        assertEquals("hostname mismatch", "dupaddress", config.getAddress());
        assertEquals("login mismatch", "duplogin", config.getLogin());
        assertEquals("password mismatch", "duppass", config.getPassword());
    }

    @Test
    public void testUseNameAsAddress() throws IOException {
        VshRC rc = createAndLoadVshRC(ADDR_HOST);
        HostConfig config = rc.getHostConfig("addrhost");
        assertEquals("name mismatch", "addrhost", config.getName());
        assertTrue("address not null", config.getAddress() == null);
        assertEquals("login mismatch", "addrlogin", config.getLogin());
        assertEquals("password mismatch", "addrpass", config.getPassword());
    }

    @Test
    public void testInitiationWithOptionals() throws IOException {
        VshRC rc = new VshRC(Util.createTempFile("vshrc-"));
        rc.getRCFile().delete();
        assert (!rc.getRCFile().exists());
        StringReader in = new StringReader(NEWLINE + "inithost" + NEWLINE
                + "initlogin" + NEWLINE + "initpass" + NEWLINE + "initaddr"
                + NEWLINE + NEWLINE);
        StringWriter out = new StringWriter();
        rc.initViaPrompt(in, out);
        HostConfig expected = new HostConfig("inithost") {
            {
                setLogin("initlogin");
                setPassword("initpass");
                setAddress("initaddr");
            }
        };
        assertEquals("init config mismatch", expected,
                rc.getHostConfig("inithost"));
    }

    @Test
    public void testInitiationWithoutOptionals() throws IOException {
        VshRC rc = new VshRC(Util.createTempFile("vshrc-"));
        rc.getRCFile().delete();
        assert (!rc.getRCFile().exists());
        StringReader in = new StringReader(NEWLINE + "inithost" + NEWLINE
                + "initlogin" + NEWLINE + "initpass" + NEWLINE + NEWLINE
                + NEWLINE);
        StringWriter out = new StringWriter();
        rc.initViaPrompt(in, out);
        HostConfig expected = new HostConfig("inithost") {
            {
                setLogin("initlogin");
                setPassword("initpass");
            }
        };
        HostConfig actual = rc.getHostConfig("inithost"); 
        assertEquals("init config mismatch", expected, actual);
    }

    private static File createRCFile(String content) throws IOException {
        try {
            File rc = Util.createTempFile("vshrc-");
            rc.deleteOnExit();
            if (content != null) {
                OutputStream out = new FileOutputStream(rc);
                try {
                    out.write(content.getBytes());
                } finally {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        // OK
                    }
                }
            }
            return rc;
        } catch (IOException ex) {
            System.err
                    .println("YIKES! Error creating vshrc file, test is probably foobar'd: "
                            + ex);
            throw ex;
        }
    }

    private static VshRC createAndLoadVshRC(String content) throws IOException {
        VshRC vshrc = new VshRC(createRCFile(content));
        try {
            vshrc.load();
        } catch (IOException ex) {
            System.err
                    .println("YIKES! Error loading vshrc file, test is probably foobar'd: "
                            + ex);
            throw ex;
        }
        return vshrc;
    }

}
