package com.vmware.vsh;

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
