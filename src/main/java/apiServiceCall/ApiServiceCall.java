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
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;
import java.util.Date;


public class ApiServiceCall {

    public static ApiServiceConfiguration configuration = new ApiServiceConfiguration();
    private static Connection conn = PostgresSQLConnector.getConnection();

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
            System.out.println("API Server Response Status Code: " +response.getStatusLine().getStatusCode());


            /*
            Implementation of JDBC PostgreSQL prepared statements
            https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
            */
            String responseCode = String.valueOf(String.valueOf(response.getStatusLine().getStatusCode()));

            /*
            Prepared statement template is implemented with the API configuration values and corresponding database table columns
            */

            PreparedStatement preparedStatement = null;
            //Prepared statement query
            preparedStatement = conn.prepareStatement("INSERT into public.api_dashboard (http_header, api_uri, status_code_response, timestamp) VALUES (?,?,?,?);");
            /*
            Prepared statement API request variables are set below.
            */
            preparedStatement.setString(1, method);
            preparedStatement.setString(2, thisUrl);
            preparedStatement.setString(3, responseCode);
            preparedStatement.setTimestamp(4, new Timestamp(new Date().getTime()));

            //Executing and subsequently closing the statement after execution
            preparedStatement.execute();
            preparedStatement.close();

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

                getRequest.addHeader("Authorization", GenAuthorizationHeader(url,"", null, HttpGet.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey));
                response = client.execute(getRequest);

                break;
            case "POST":

                json = ApiServiceConfiguration.JSON;
                HttpPost postRequest = new HttpPost(url);
                postRequest.addHeader("Authorization", GenAuthorizationHeader(url, json,null, HttpPost.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey));
                postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                response = client.execute(postRequest);

                break;
            default:
                method = "GET";
        }
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

    public static String GenAuthorizationHeader(String requestUrl, String requestContent, String requestParams,
                                                String httpMethod, String p12Content, String p12Password, String keyAlias, String consumerKey) throws Exception {

        //Authorization header string.
        Charset charset = StandardCharsets.UTF_8;

        InputStream is = new FileInputStream(ApiServiceConfiguration.P12);
        SetP12(is, keyAlias, p12Password);
        URI uri = URI.create(requestUrl);
        OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);

        String authHeader = OAuth.getAuthorizationHeader(uri, httpMethod, requestContent, charset, consumerKey, signingKey);
        System.out.println("HTTP Method: " + httpMethod);
        System.out.println("API Request URI: " + uri);
        System.out.println("Request Authorization Header: " + authHeader);
        return authHeader;
    }

    private static void SetP12(InputStream is, String alias, String password) throws SdkException {
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

