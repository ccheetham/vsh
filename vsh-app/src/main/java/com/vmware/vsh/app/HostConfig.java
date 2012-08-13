package com.vmware.vsh.app;

/**
 * <code>HostConfig</code> contains the information needed to access a remote
 * service.
 * 
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class HostConfig {

    private final String name;

    private String address;

    private String login;

    private String password;


    /**
     * Create a new <code>HostConfig</code> of the specified name.
     * 
     * @param name configuration name
     */
    public HostConfig(String name) {
        assert(name != null);
        this.name= name;
    }

    /**
     * Return the name of this configuration.
     * 
     * @return configuration name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the remote service's address.
     * 
     * @param address remote service address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Return the remote service's address.
     * 
     * @return remote service address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the user name to be used to login to the remote service.
     * 
     * @param login
     *            remote service user login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Return the user name to be used to login to the remote service.
     * 
     * @return remote service user login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Return the user name to be used to login to the remote service.
     * 
     * @param password
     *            remote service user password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Return the user password to be used to login to the remote service.
     * 
     * @return remote service user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Return <code>true</code> if <code>o</code> is logically equal to this
     * instance.
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HostConfig)) {
            return false;
        }
        HostConfig that = (HostConfig)o;
        if (!equals(this.name, that.name)) {
            return false;
        }
        if (!equals(this.login, that.login)) {
            return false;
        }
        if (!equals(this.password, that.password)) {
            return false;
        }
        if (!equals(this.address, that.address)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{").append(name).append(":");
        buf.append(login);
        buf.append(",").append(password == null ? password : "*");
        buf.append(",").append(address);
        buf.append("}");
        return buf.toString();
    }

    private static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}
