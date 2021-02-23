import com.mastercard.api.core.exception.SdkException;
import com.mastercard.developer.oauth.OAuth;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;


//Next steps:
//- Resolve dependencies to get this file compiled
//- Add p12 files and make authenticated API calls
//- Change structure to make dynamic, separate config, separate functions to call APIs
//- Add Postgres database support


public class PANRegistrationTestService {
    private static String[] SUPPORTED_TLS = new String[] { "TLSv1.1", "TLSv1.2" };

    private static final String STAGE_SANDBOX_CONSUMERR_KEY = "nhuDyZeFyJDwGYBe71E9qy6nJ-v0x9W_bZqsy9kq91f33083!ddc6accfb9c24a078e8e742a0f1bdf7e0000000000000000";
    private static final String STAGE_SANDBOX_KEY_ALIAS = "keyalias";
    private static final String STAGE_SANDBOX_PASSWORD = "keystorepassword";
    private static final String STAGE_SANDBOX_URL = "https://sbx.stage.api.mastercard.com/alm/panregistration/accounts";
    private static final String STAGE_SANDBOX_P12_STRING = "";

    private static final String STAGE_PROD_CONSUMERR_KEY = "quxSP1UwsBykH1hemN3S4v7FXIjrSij21DDQCr7t2938077b!0dd7e3863ee94034bb92cd1e97b981ea0000000000000000";
    private static final String STAGE_PROD_KEY_ALIAS = "prodAlias";
    private static final String STAGE_PROD_PASSWORD = "keystore1234";
    private static final String STAGE_PROD_URL = "https://stage.api.mastercard.com/alm/panregistration/accounts";
    private static final String STAGE_PROD_P12_STRING = "";


    private static PrivateKey signingKey;

    public static void main(String[] args) throws Exception {

        String consumerKey =  STAGE_PROD_CONSUMERR_KEY;
        String keyAlias = STAGE_PROD_KEY_ALIAS;
        String keyPassword = STAGE_PROD_PASSWORD;
        String thisUrl = STAGE_PROD_URL;
        String p12String = STAGE_PROD_P12_STRING;

        try {
            HttpResponse response = getResponse(thisUrl, p12String, keyPassword, keyAlias, consumerKey);
            Header[] thisResponseHeaderArray = response.getAllHeaders();
            System.out.println("Response Header:");
            for (int i=0; i<thisResponseHeaderArray.length; i++) {
                System.out.println(thisResponseHeaderArray[i].toString());
            }
            System.out.println("Response is: "+ EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static HttpResponse getResponse(String url, String p12Content, String p12Password, String keyAlias, String consumerKey) throws Exception {
        HttpClientBuilder builder = httpBuilder();
        HttpClient client = builder.build();
        //For POST
        String json = "{\"requestId\":\"hSJA-12197987-shfksdjhks-15289\",\"totalRecords\":\"1\",\"records\": [{\"pan\":\"8787628778867856\"},{\"pan\":\"22787628778867856\"}]}";
        HttpPost postRequest = new HttpPost(url);
        postRequest.addHeader("Authorization", genAuthorizationHeader(url, json, null, HttpPost.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey));
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(postRequest);

        //For GET
        /*HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        //LOG.info("Authorization header = " + LocationsTestService.genAuthorizationHeader());
        getRequest.addHeader("Authorization", genAuthorizationHeader(url, "", null, HttpGet.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey));
        HttpResponse response = client.execute(getRequest);*/
        //LOG.info(response.toString());
        return response;
    }

    @SuppressWarnings("deprecation")
    private static HttpClientBuilder httpBuilder() throws KeyManagementException, NoSuchAlgorithmException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.useSystemProperties();
        builder.disableCookieManagement();

        final String[] supportedProtocols = SUPPORTED_TLS;

        SSLContext sslContext = SSLContext.getInstance("SSL");
        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[]{ new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

            @SuppressWarnings("unused")
            public boolean isClientTrusted(X509Certificate[] arg0) {
                return false;
            }

            @SuppressWarnings("unused")
            public boolean isServerTrusted(X509Certificate[] arg0) {
                return false;
            }
        } }, new SecureRandom());

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, supportedProtocols, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        builder.setSSLSocketFactory(sslsf);

        return builder;
    }

    public static String genAuthorizationHeader(String requestUrl, String requestContent, String requestParams,
                                                String httpMethod, String p12Content, String p12Password, String keyAlias, String consumerKey) throws Exception {

        //Authorization header string.
        Charset charset = StandardCharsets.UTF_8;
        // Get Private Key Via P12
        InputStream is = new FileInputStream("/Users/conordixon/p12");
        setP12(is, keyAlias, p12Password);
        URI uri = URI.create(requestUrl);
        OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);

        String authHeader = OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);
        System.out.println("Request Authorization Header:: " + authHeader);
        return authHeader;
    }

    private static void setP12(InputStream is, String alias, String password) throws SdkException {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(is, password.toCharArray());
            signingKey = (PrivateKey)ks.getKey(alias, password.toCharArray());
            if (signingKey == null) {
                throw new SdkException("No key found for alias [" + alias + "]");
            }
        } catch (Exception var5) {
            throw new SdkException(var5.getMessage(), var5);
        }
    }
}

