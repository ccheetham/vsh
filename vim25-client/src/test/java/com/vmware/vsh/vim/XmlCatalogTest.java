package com.vmware.vsh.vim;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.ws.WebServiceException;

import org.junit.Test;

import com.vmware.vsh.vim.v25.VimService;

public class XmlCatalogTest {

    @Test
    public void testCatalogFound() {
        try {
            XmlCatalog.init();
            new VimService();
        } catch (FileNotFoundException ex) {
            if (XmlCatalog.WSDL_ZIP_PATH.equals(ex.getMessage())) {
                System.err.println("cannot find " + XmlCatalog.WSDL_ZIP_PATH);
                System.err.println("maybe not using using distribution jar?");
                System.err.println("tentatively passing this test");
            } else {
                fail("file not found: " + ex);
            }
        } catch (IOException ex) {
            fail("io error: " + ex);
        } catch (WebServiceException ex) {
            fail("vim25 catalog not found");
        }
    }

}
