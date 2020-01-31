package org.qinarmy.foundation.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SimpleTests {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTests.class);


    @Test
    public void address() throws Exception {
        for (Enumeration<NetworkInterface> nfs = NetworkInterface.getNetworkInterfaces(); nfs.hasMoreElements(); ) {
            NetworkInterface nf = nfs.nextElement();

            for (Enumeration<InetAddress> addrs = nf.getInetAddresses(); addrs.hasMoreElements(); ) {
                InetAddress address = addrs.nextElement();
                printAddress(address);
            }
        }
    }

    @Test
    public void publicAddress() throws Exception {
        printAddress(InetAddress.getByName("180.101.49.11"));
    }

    private static void printAddress(InetAddress address) {
        LOG.info("\naddress:{}\nisMCOrgLocal:{}\nisSiteLocalAddress:{}\nisAnyLocalAddress:{}\nisLinkLocalAddress:{}\nisMCGlobal:{}\nisLoopbackAddress:{}\nisMCNodeLocal:{}\nisMCSiteLocal:{}\nisMulticastAddress:{}\nisMCLinkLocal:{}\n"
                , address.getHostAddress()

                , address.isMCOrgLocal()
                , address.isSiteLocalAddress()
                , address.isAnyLocalAddress()
                , address.isLinkLocalAddress()

                , address.isMCGlobal()
                , address.isLoopbackAddress()
                , address.isMCNodeLocal()
                , address.isMCSiteLocal()

                , address.isMulticastAddress()
                , address.isMCLinkLocal()


        );
    }

}
