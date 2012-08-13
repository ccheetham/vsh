package com.vmware.vsh.app;

/**
 * <code>About</code> provides informational details about
 * <code>com.vmware.vsh.*</code> packages.
 *
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class About {

    public static void main(String[] args) {
        System.out.println("title  : " + getTitle());
        System.out.println("vendor : " + getVendor());
        System.out.println("version: " + getVersion());
    }

    public static String getVersion() {
        return About.class.getPackage().getImplementationVersion();
    }

    public static String getVendor() {
        return About.class.getPackage().getImplementationVendor();
    }

    public static String getTitle() {
        return About.class.getPackage().getImplementationTitle();
    }

}
