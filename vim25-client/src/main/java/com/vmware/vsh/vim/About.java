package com.vmware.vsh.vim;

/**
 * <code>About</code> provides informational details about
 * <code>com.vmware.vsh.vim.*</code> packages.
 *
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class About {

    public static void main(String[] args) {
        System.out.println("title  : " + getTitle());
        System.out.println("vendor : " + getVendor());
        System.out.println("version: " + getVersion());
    }

    /**
     * Return Vim25 client library version.
     * @return library version
     */
    public static String getVersion() {
        return About.class.getPackage().getImplementationVersion();
    }

    /**
     * Return Vim25 client library vendor.
     * @return library vendor
     */
    public static String getVendor() {
        return About.class.getPackage().getImplementationVendor();
    }

    /**
     * Return Vim25 client library title.
     * @return library title
     */
    public static String getTitle() {
        return About.class.getPackage().getImplementationTitle();
    }

}
