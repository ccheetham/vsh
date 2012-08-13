package com.vmware.vsh.vim;

import org.junit.Test;

public class AboutTest {

    @Test
    public void testVersion() {
        About.getVersion();
    }

    @Test
    public void testVendor() {
        About.getVendor();
    }

    @Test
    public void testTitle() {
        About.getTitle();
    }

}
