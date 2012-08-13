package com.vmware.vsh.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HostConfigTest {

    private HostConfig config;

    @Before
    public void setUp() {
        config = new HostConfig("testname");
    }

    @Test
    public void testName() {
        assertEquals("name mismatch", "testname", config.getName());
    }

    @Test
    public void testAddress() {
        config.setAddress("testaddr");
        assertEquals("address mismatch", "testaddr", config.getAddress());
    }

    @Test
    public void testMissingAddress() {
        assertTrue("address not null", config.getAddress() == null);
    }

    @Test
    public void testLogin() {
        config.setLogin("testuser");
        assertEquals("login mismatch", "testuser", config.getLogin());
    }

    @Test
    public void testPassword() {
        config.setPassword("testpass");
        assertEquals("password mismatch", "testpass", config.getPassword());
    }
    
    @Test
    public void testEquality() {
        HostConfig a1 = new HostConfig("a");
        // non HostConfig thingies
        assertFalse("expected != null", a1.equals(null));
        assertFalse("expected != Object", a1.equals(new Object()));
        // a target HostConfig with different name
        assertFalse("expected != cfg diff name", a1.equals(new HostConfig("b")));
        // a target HostConfig 
        a1.setLogin("login");
        a1.setPassword("pass");
        a1.setAddress("addr");
        HostConfig a2 = new HostConfig(a1.getName());
        a2.setLogin(a1.getLogin());
        a2.setPassword(a1.getPassword());
        a2.setAddress(a1.getAddress());
        assertTrue("expected == cfg a2", a1.equals(a2));
        // a target HostConfig with various things null
        a2.setLogin(null);
        assertFalse("expected != cfg a2 (login null)", a1.equals(a2));
        a2.setLogin(a1.getLogin());
        a2.setPassword(null);
        assertFalse("expected != cfg a2 (password null)", a1.equals(a2));
        a2.setPassword(a1.getPassword());
        a2.setAddress(null);
        assertFalse("expected != cfg a2 (address null)", a1.equals(a2));
        // a source HostConfig with various things null
        a2.setAddress(a1.getAddress());
        a1.setLogin(null);
        assertFalse("expected != cfg a2 (source login null)", a1.equals(a2));
        a1.setLogin(a2.getLogin());
        a1.setPassword(null);
        assertFalse("expected != cfg a2 (source password null)", a1.equals(a2));
        a1.setPassword(a2.getPassword());
        a1.setAddress(null);
        assertFalse("expected != cfg a2 (source address null)", a1.equals(a2));
   }

}
