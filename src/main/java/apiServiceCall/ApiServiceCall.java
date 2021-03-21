package apiServiceCall;


import PostgresSQLConnector.PostgresSQLConnector;
import java.sql.Connection;
import apiConfiguration.ApiServiceConfiguration;
import com.mastercard.api.core.exception.SdkException;
import com.mastercard.developer.oauth.OAuth;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;


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
import static org.slf4j.LoggerFactory.getLogger;


//Next steps:
//- Resolve dependencies to get this file compiled
//- Add p12 files and make authenticated API calls
//- Change structure to make dynamic, separate config, separate functions to call APIs
//- Add Postgres database support

/**
 * Next steps 2-Mar-2021
 *
 * 1) Lines 43-53 we need to move them to external file (for now Java). "ApiServiceConfiguration or API config or something"
 * This should go to separate class. And then you just instantiate this class here.
 * Configurations.class.
 * In this file here, you have to do something like: Configurations configuration = new ApiServiceConfiguration();
 * For now - just for Locations. Next meeting - we will parameterize for different APIs.
 *
 * Move these strings as well:
 *  String json = "{\"requestId\":\"hSJA-12197987-shfksdjhks-15289\",\"totalRecords\":\"1\",\"records\": [{\"pan\":\"8787628778867856\"},{\"pan\":\"22787628778867856\"}]}";
 *
 * configuration.SANDBOX_CONSUMER_KEY
 *
 * 2) Add package (to each and every class you create)
 *
 * 3) Change the name of the class  ApiServiceCall.ApiServiceCall > change to generic - APICallClass.
 *
 * FOR LATER
 * 1) Create switch statement for POST/GET
 * 2) Deprecation fixing
 * 3) Saving response in DB - (check Crawler class from QA framework).
 * 4) Some sort of OK response from the GW.
 *
 * */
//Match or some other API - whatever suits.
//Fill this with all the other APIs and have GET calls sorted.
//Once the calls are working, think about adding results to DB.
//How many APIs do want to incorporate?
//Setup your local instance of PostgreSql
//Prepare login data for DB url/user/pass
//Copy over the PostgreSqlConnector class into your project.
//Look at DevPortalCrawler class how connections are done -> then we will have another meeting.

//Low priority for now:
//POST calls, how important.

import static org.slf4j.LoggerFactory.getLogger;

public class ApiServiceCall {

    public static ApiServiceConfiguration configuration = new ApiServiceConfiguration();
    private static Connection conn = PostgresSQLConnector.getConnection();
    private static final Logger LOGGER = getLogger(ApiServiceCall.class);

    private static String[] SUPPORTED_TLS = new String[] { "TLSv1.1", "TLSv1.2" };

    public static PrivateKey signingKey;

    public static void main(String[] args) throws Exception {

        ApiGetCall(ApiServiceConfiguration.SANDBOX_OAUTH_KEY, ApiServiceConfiguration.LOCATIONS_SANDBOX_ENDPOINT, "GET");
        ApiGetCall(ApiServiceConfiguration.SANDBOX_OAUTH_KEY, ApiServiceConfiguration.MATCH_SANDBOX_ENDPOINT, "POST");


    }

    public static void ApiGetCall(String consKey, String apiUrl, String method) {

        String consumerKey =  consKey;
        String keyAlias = ApiServiceConfiguration.SANDBOX_OAUTH_KEY_ALIAS;;
        String keyPassword = ApiServiceConfiguration.SANDBOX_OAUTH_KEY_PASSWORD;
        String thisUrl = apiUrl;
        String p12String = ApiServiceConfiguration.SANDBOX_P12_STRING;
        String json = ApiServiceConfiguration.JSON;

        try {
            HttpResponse response = getResponse(thisUrl, p12String, keyPassword, keyAlias, consumerKey, json, method);
            Header[] thisResponseHeaderArray = response.getAllHeaders();
            System.out.println("API Response Code: " +response.getStatusLine().getStatusCode());
            System.out.println("API Protocol Version: " +response.getStatusLine().getProtocolVersion());
            System.out.println("API Response Headers: ");
            for (int i=0; i<thisResponseHeaderArray.length; i++) {
                System.out.println(thisResponseHeaderArray[i].toString());
            }
            System.out.println("Response is: "+ EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpResponse getResponse(String url, String p12Content, String p12Password, String keyAlias, String consumerKey, String json, String method) throws Exception {
        HttpClientBuilder builder = httpBuilder();
        HttpClient client = builder.build();
        HttpGet getRequest = new HttpGet(url);
        HttpResponse response = null;

        switch(method) {
            case  "GET":

                getRequest.addHeader("Authorization", GenAuthorizationHeader(url,"", null, HttpGet.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey, json));
                response = client.execute(getRequest);

                break;
            case "POST":

                json = ApiServiceConfiguration.JSON;
                HttpPost postRequest = new HttpPost(url);
                postRequest.addHeader("Authorization", GenAuthorizationHeader(url, json, null, HttpPost.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey, json));
                postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                response = client.execute(postRequest);

                break;
            default:
                method = "GET";
        }
        return response;
//    } catch (Exception e) {
//        throw Exception(LOGGER.info(logPrefix + "API call was not successful"));
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

    public static String GenAuthorizationHeader(String requestUrl, String requestContent, String requestParams,
                                                String httpMethod, String p12Content, String p12Password, String keyAlias, String consumerKey, String responses) throws Exception {

        //Authorization header string.
        Charset charset = StandardCharsets.UTF_8;

        InputStream is = new FileInputStream(ApiServiceConfiguration.P12);
        setP12(is, keyAlias, p12Password);
        URI uri = URI.create(requestUrl);
        OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);

        String authHeader = OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);
        System.out.println("Request Authorization Header: " + authHeader);
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

