package com.vmware.vsh;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.soap.SOAPFaultException;

import com.vmware.vsh.vim.XmlCatalog;
import com.vmware.vsh.vim.v25.DynamicProperty;
import com.vmware.vsh.vim.v25.ManagedObjectReference;
import com.vmware.vsh.vim.v25.ObjectContent;
import com.vmware.vsh.vim.v25.ObjectSpec;
import com.vmware.vsh.vim.v25.PropertyFilterSpec;
import com.vmware.vsh.vim.v25.PropertySpec;
import com.vmware.vsh.vim.v25.RetrieveOptions;
import com.vmware.vsh.vim.v25.RetrieveResult;
import com.vmware.vsh.vim.v25.SelectionSpec;
import com.vmware.vsh.vim.v25.ServiceContent;
import com.vmware.vsh.vim.v25.TraversalSpec;
import com.vmware.vsh.vim.v25.VimPortType;
import com.vmware.vsh.vim.v25.VimService;

public class VSphereClient {

    static {
        try {
            System.out.println("https setup");
            HostnameVerifier hv = new HostnameVerifier() {
                @Override
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            };
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
            javax.net.ssl.TrustManager tm = new TrustAllTrustManager();
            trustAllCerts[0] = tm;
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            javax.net.ssl.SSLSessionContext sslsc = sc.getServerSessionContext();
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (KeyManagementException ex) {
            Logger.getLogger(VSphereClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(VSphereClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new ExceptionInInitializerError(ex);
        }
        try {
            XmlCatalog.init();
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void connect(String url, String username, String password) {
        try {
            ManagedObjectReference instRef = new ManagedObjectReference();
            instRef.setType("ServiceInstance");
            instRef.setValue("ServiceInstance");

            WebServiceClient ann = VimService.class.getAnnotation(WebServiceClient.class);
            String wsdlLocation = ann.wsdlLocation();
            URL wsdlUrl = VimService.class.getResource(wsdlLocation);
            QName serviceName = new QName(ann.targetNamespace(), ann.name());
            VimService svc = new VimService(wsdlUrl, serviceName);
            VimPortType vimPort = svc.getVimPort();

            Map<String, Object> ctx = ((BindingProvider)vimPort).getRequestContext();
            ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
            ctx.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

            ServiceContent content = vimPort.retrieveServiceContent(instRef);

            System.out.println("logging in");
            vimPort.login(content.getSessionManager(), username, password, null);

            ManagedObjectReference propCollector = content.getPropertyCollector();
            ManagedObjectReference rootRef = content.getRootFolder();

            TraversalSpec resourcePoolTraversalSpec = new TraversalSpec();
            resourcePoolTraversalSpec.setName("resourcePoolTraversalSpec");
            resourcePoolTraversalSpec.setType("ResourcePool");
            resourcePoolTraversalSpec.setPath("resourcePool");
            resourcePoolTraversalSpec.setSkip(Boolean.FALSE);
            SelectionSpec rpts = new SelectionSpec();
            rpts.setName("resourcePoolTraversalSpec");
            resourcePoolTraversalSpec.getSelectSet().add(rpts);

            TraversalSpec computeResourceRpTraversalSpec = new TraversalSpec();
            computeResourceRpTraversalSpec.setName("computeResourceRpTraversalSpec");
            computeResourceRpTraversalSpec.setType("ComputeResource");
            computeResourceRpTraversalSpec.setPath("resourcePool");
            computeResourceRpTraversalSpec.setSkip(Boolean.FALSE);
            SelectionSpec rptss = new SelectionSpec();
            rptss.setName("resourcePoolTraversalSpec");
            computeResourceRpTraversalSpec.getSelectSet().add(rptss);

            TraversalSpec computeResourceHostTraversalSpec = new TraversalSpec();
            computeResourceHostTraversalSpec.setName("computeResourceHostTraversalSpec");
            computeResourceHostTraversalSpec.setType("ComputeResource");
            computeResourceHostTraversalSpec.setPath("host");
            computeResourceHostTraversalSpec.setSkip(Boolean.FALSE);

            TraversalSpec datacenterHostTraversalSpec = new TraversalSpec();
            datacenterHostTraversalSpec.setName("datacenterHostTraversalSpec");
            datacenterHostTraversalSpec.setType("Datacenter");
            datacenterHostTraversalSpec.setPath("hostFolder");
            datacenterHostTraversalSpec.setSkip(Boolean.FALSE);
            SelectionSpec ftspec = new SelectionSpec();
            ftspec.setName("folderTraversalSpec");
            datacenterHostTraversalSpec.getSelectSet().add(ftspec);

            TraversalSpec datacenterVmTraversalSpec = new TraversalSpec();
            datacenterVmTraversalSpec.setName("datacenterVmTraversalSpec");
            datacenterVmTraversalSpec.setType("Datacenter");
            datacenterVmTraversalSpec.setPath("vmFolder");
            datacenterVmTraversalSpec.setSkip(Boolean.FALSE);
            SelectionSpec ftspecs = new SelectionSpec();
            ftspecs.setName("folderTraversalSpec");
            datacenterVmTraversalSpec.getSelectSet().add(ftspecs);

            TraversalSpec folderTraversalSpec = new TraversalSpec();
            folderTraversalSpec.setName("folderTraversalSpec");
            folderTraversalSpec.setType("Folder");
            folderTraversalSpec.setPath("childEntity");
            folderTraversalSpec.setSkip(Boolean.FALSE);
            SelectionSpec ftrspec = new SelectionSpec();
            ftrspec.setName("folderTraversalSpec");
            List<SelectionSpec> ssarray = new ArrayList<SelectionSpec>();
            ssarray.add(ftrspec);
            ssarray.add(datacenterHostTraversalSpec);
            ssarray.add(datacenterVmTraversalSpec);
            ssarray.add(computeResourceRpTraversalSpec);
            ssarray.add(computeResourceHostTraversalSpec);
            ssarray.add(resourcePoolTraversalSpec);

            folderTraversalSpec.getSelectSet().addAll(ssarray);
            PropertySpec props = new PropertySpec();
            props.setAll(Boolean.FALSE);
            props.getPathSet().add("name");
            props.setType("ManagedEntity");
            List<PropertySpec> propspecary = new ArrayList<PropertySpec>();
            propspecary.add(props);

            PropertyFilterSpec spec = new PropertyFilterSpec();
            spec.getPropSet().addAll(propspecary);

            spec.getObjectSet().add(new ObjectSpec());
            spec.getObjectSet().get(0).setObj(rootRef);
            spec.getObjectSet().get(0).setSkip(Boolean.FALSE);
            spec.getObjectSet().get(0).getSelectSet().add(folderTraversalSpec);

            List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
            listpfs.add(spec);
            List<ObjectContent> listobjcont = retrievePropertiesAllObjects(vimPort, propCollector, listpfs);

    // If we get contents back. print them out.
            if (listobjcont != null) {
                ObjectContent oc = null;
                ManagedObjectReference mor = null;
                DynamicProperty pc = null;
                for (int oci = 0; oci < listobjcont.size(); oci++) {
                    oc = listobjcont.get(oci);
                    mor = oc.getObj();

                    List<DynamicProperty> listdp = oc.getPropSet();
                    System.out.println("Object Type : " + mor.getType());
                    System.out.println("Reference Value : " + mor.getValue());

                    if (listdp != null) {
                        for (int pci = 0; pci < listdp.size(); pci++) {
                            pc = listdp.get(pci);
                            System.out.println("   Property Name : " + pc.getName());
                            if (pc != null) {
                                if (!pc.getVal().getClass().isArray()) {
                                    System.out.println("   Property Value : " + pc.getVal());
                                } else {
                                    List<Object> ipcary = new ArrayList<Object>();
                                    ipcary.add(pc.getVal());
                                    System.out.println("Val : " + pc.getVal());
                                    for (int ii = 0; ii < ipcary.size(); ii++) {
                                        Object oval = ipcary.get(ii);
                                        if (oval.getClass().getName().indexOf(
                                                "ManagedObjectReference") >= 0) {
                                            ManagedObjectReference imor = (ManagedObjectReference) oval;

                                            System.out.println("Inner Object Type : "
                                                    + imor.getType());
                                            System.out.println("Inner Reference Value : "
                                                    + imor.getValue());
                                        } else {
                                            System.out.println("Inner Property Value : " + oval);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("No Managed Entities retrieved!");
            }
  
            System.out.println("done");
        } catch (Exception ex) {
            Logger.getLogger(VSphereClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   private static List<ObjectContent> retrievePropertiesAllObjects(
           VimPortType vimPort, ManagedObjectReference propCollectorRef, List<PropertyFilterSpec> listpfs)
      throws Exception {

      RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

      List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

      try {
         RetrieveResult rslts =
            vimPort.retrievePropertiesEx(propCollectorRef,
                                         listpfs,
                                         propObjectRetrieveOpts);
         if (rslts != null && rslts.getObjects() != null &&
               !rslts.getObjects().isEmpty()) {
            listobjcontent.addAll(rslts.getObjects());
         }
         String token = null;
         if(rslts != null && rslts.getToken() != null) {
            token = rslts.getToken();
         }
         while (token != null && !token.isEmpty()) {
            rslts = vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
            token = null;
            if (rslts != null) {
               token = rslts.getToken();
               if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                 listobjcontent.addAll(rslts.getObjects());
               }
            }
         }
      } catch (SOAPFaultException sfe) {
         printSoapFaultException(sfe);
      } catch (Exception e) {
         System.out.println(" : Failed Getting Contents");
         e.printStackTrace();
      }

      return listobjcontent;
   }

   private static void printSoapFaultException(SOAPFaultException sfe) {
      System.out.println("SOAP Fault -");
      if (sfe.getFault().hasDetail()) {
         System.out.println(sfe.getFault().getDetail().getFirstChild().getLocalName());
      }
      if (sfe.getFault().getFaultString() != null) {
         System.out.println("\n Message: " + sfe.getFault().getFaultString());
      }
   }

   private static class TrustAllTrustManager implements javax.net.ssl.TrustManager,
                                                        javax.net.ssl.X509TrustManager {

      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
         return null;
      }

//      public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
//         return true;
//      }
//
//      public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
//         return true;
//      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                                     String authType)
         throws java.security.cert.CertificateException {
         return;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                                     String authType)
         throws java.security.cert.CertificateException {
         return;
      }
   }

}
