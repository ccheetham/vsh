package com.vmware.vsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

import org.junit.Test;

public class AboutTest {

    @Test
    public void testVersion() {
        String version = About.getVersion();
        assumeNotNull(version);
        assertEquals("version mismatch", "0.1", version);
    }

    @Test
    public void testVendor() {
        String vendor = About.getVendor();
        assumeNotNull(vendor);
        assertEquals("vendor mismatch", "VMware", vendor);
    }

    @Test
    public void testTitle() {
        String title = About.getTitle();
        assumeNotNull(title);
        assertEquals("title mismatch", "VSH API", title);
    }

}
