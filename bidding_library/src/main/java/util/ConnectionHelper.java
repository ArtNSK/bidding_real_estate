package util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.X509Certificate;

@UtilityClass
@Slf4j
public class ConnectionHelper {

    public URLConnection getUrlConnection(String urlString) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        doTrustToCertificates();
        URL url = new URL(urlString);

        return url.openConnection();
    }

    private void doTrustToCertificates() throws NoSuchAlgorithmException, KeyManagementException  {
        Security.addProvider(new Provider("provider", "1.0", "info") {
            @Override
            public Provider configure(String configArg) {
                return super.configure(configArg);
            }
        });
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = (urlHostName, session) -> {
            if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                log.info("Warning: URL host '{}' is different to SSLSession host '{}'" , urlHostName, session.getPeerHost());
            }
            return true;
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}
