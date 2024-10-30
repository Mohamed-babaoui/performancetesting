import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;
import java.security.cert.X509Certificate;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.HttpsURLConnection;
import java.security.KeyStore;
import java.io.FileInputStream;

public class ClassWithMain {
    public static void main(String[] args) {

        try {
            // Step 1: Set up SSLContext to trust all certificates
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((X509Certificate[] chain, String authType) -> true)
                    .build();

            // Step 2: Apply SSL Context globally for HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Step 3: Use RemoteWebDriver with the desired capabilities
            DesiredCapabilities capabilities = new DesiredCapabilities();
            System.setProperty("javax.net.ssl.trustStore", "src" + File.separator + "pvcp-intermidate.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "97460480");
            WebDriver driver = new RemoteWebDriver(new URL("https://selenium-grid-acc.aks-acc.pvcp.intra/wd/hub"), capabilities);

            // Use the driver as needed
            driver.get("https://example.com");
            System.out.println("Title: " + driver.getTitle());

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
