package com.vmware.vsh.vim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <code>XMLCatalog</code> performs XML conveniences for the Vim client library.
 * Currently the only convenience is the setup of a temporary catalog by the
 * internal XML parser when locating Vim WSDL files at runtime.
 * 
 * @author <a href="mailto:ccheetham@vmware.com">Chris Cheetham, VMware</a>
 */
public class XmlCatalog {

    private static final Logger LOG = Logger.getLogger(XmlCatalog.class
            .getName());

    /**
     * The path to the zip file containing the library's WSDL files.
     */
    public static final String WSDL_ZIP_PATH = "/wsdl.zip";

    /**
     * The directory into which runtime files will be written. The path is
     * generated at class load time using a UUID so multiple JVM instances each
     * running different versions of this class can run concurrently without
     * danger of collision.
     */
    public static final File RUNTIME_DIR;
    static {
        RUNTIME_DIR = new File(System.getProperty("java.io.tmpdir"),
                "vimclient-" + UUID.randomUUID());
    }

    /**
     * The runtime file path of the XML catalog. The catalog contains the
     * runtime paths of the Vim client WSDL files.
     */
    public static final File XML_CATALOG;
    static {
        XML_CATALOG = new File(RUNTIME_DIR, "catalog.xml");
    }

    /**
     * Install the WSDL files included with the library. The files are installed
     * into a temporary directory, {@link XmlCatalog#RUNTIME_DIR}.
     */
    public static void init() throws IOException {
        // wsdl is stored in a zip file in the distribution jar
        InputStream in = XmlCatalog.class.getResourceAsStream(WSDL_ZIP_PATH);
        if (in == null) {
            throw new FileNotFoundException(WSDL_ZIP_PATH);
        }
        try {
            LOG.info("loading wsdl from " + WSDL_ZIP_PATH);
            ZipInputStream zip = new ZipInputStream(in);
            // wsdl to go into a temp dir that'll be deleted upon jvm exit
            LOG.info("writing wsdl to " + RUNTIME_DIR
                    + " (will be deleted on exit)");
            RUNTIME_DIR.mkdir();
            RUNTIME_DIR.deleteOnExit();
            // unzip the wsdl archive
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                LOG.fine("extracting " + entry.getName());
                File file = new File(RUNTIME_DIR, entry.getName());
                file.deleteOnExit();
                if (entry.isDirectory()) {
                    if (!file.exists()) {
                        if (!file.mkdir()) {
                            throw new IOException("failed to create directory "
                                    + file.getAbsolutePath());
                        }
                    }
                } else {
                    FileOutputStream out = new FileOutputStream(file);
                    try {
                        byte[] buf = new byte[1024];
                        int read;
                        while ((read = zip.read(buf)) != -1) {
                            out.write(buf, 0, read);
                        }
                    } finally {
                        try {
                            out.close();
                        } catch (IOException ex) {
                            LOG.warning("unable to output stream: " + ex);
                        }
                    }
                }
            }
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                LOG.warning("unable to close wsdl input stream: " + ex);
            }
        }
        if (!XML_CATALOG.exists()) {
            throw new FileNotFoundException(XML_CATALOG.getAbsolutePath());
        }
        // register the unzipped catalog as the new system catalog
        LOG.info("setting xml.catalog.files to " + XML_CATALOG);
        System.setProperty("xml.catalog.files", XML_CATALOG.getAbsolutePath());
    }

}
