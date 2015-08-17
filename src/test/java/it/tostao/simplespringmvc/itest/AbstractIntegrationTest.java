package it.tostao.simplespringmvc.itest;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Slawomir Leski.
 *
 */

public abstract class AbstractIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static String port;
    private static String webapp;
    private static String baseUrl;

    /**
     * Available http methods.
     */
    public enum Method {

        /** GET */
        GET,

        /** POST */
        POST,

        /** PUT */
        PUT,

        /** DELETE */
        DELETE;
    }

    private CloseableHttpClient httpClient;

    private HttpContext httpContext;

    private String lastResponseContent;

    private final Map<String, String> responseContents = new HashMap<String, String>();

    /**
     * configure port and webapp name.
     */
    @BeforeClass
    public static void getWebappNPort() {
        port = System.getProperty("webapp.port");
        webapp = System.getProperty("webapp.name");
        if (port == null) {
            port = "8080";
            LOG.debug("setting default port: 8080");
        }
        if (webapp == null) {
            webapp = "oskar-webapp";
            LOG.debug("setting default webapp: camo");
        }
        baseUrl = "http://localhost:" + port + "/" + webapp;
        LOG.debug("base url: " + baseUrl);
    }

    /**
     * build a new http client. the client does not follow redirects!
     */
    @Before
    public void createNewHttpClient() {
        LOG.debug("creating new httpClient");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                return 0;
            }
        });
        httpClientBuilder.disableRedirectHandling();
        httpClientBuilder.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        httpClient = httpClientBuilder.build();
        CookieStore cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * shut down http client
     * @throws IOException
     */
    @After
    public void tearDownHttpClient() throws IOException {
        httpClient.close();
    }

    /**
     * get a http response
     * @param method
     * @param url
     * @return
     */
    public HttpResponse getResponse(Method method, String url) {
        return getResponse(method, url, null, null);
    }

    /**
     * get a http response
     * @param method
     * @param url
     * @param entity expected to be a json string
     * @return
     */
    public HttpResponse getResponse(Method method, String url, String entity) {
        return getResponse(method, url, entity, null);
    }

    /**
     * post with postform data
     * @param method
     * @param url
     * @param postForm
     * @return
     */
    public HttpResponse getResponse(Method method, String url, Map<String, String> postForm) {
        return getResponse(method, url, null, postForm);
    }

    /**
     * get a http response
     * @param method
     * @param url
     * @param entity expected to be a json string
     * @param postForm for usage with post
     * @return
     */
    private HttpResponse getResponse(Method method, String url, String entity, Map<String, String> postForm) {
        LOG.debug("getting url: " + url);
        HttpRequestBase httpMethod = null;
        switch (method) {
        case GET:
            httpMethod = new HttpGet(url);
            break;
        case DELETE:
            httpMethod = new HttpDelete(url);
            break;
        case POST:
            HttpPost httpPost = new HttpPost(url);
            if (postForm == null) {
                throw new IllegalStateException("postform needed");
            }
            List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
            for (Entry<String, String> entry : postForm.entrySet()) {
                values.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            try {
                if (postForm.size() > 0) {
                    httpPost.setEntity(new UrlEncodedFormEntity(values));
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
            httpMethod = httpPost;
            break;
        case PUT:
            httpMethod = new HttpPut(url);
            HttpPut httpPut = (HttpPut) httpMethod;
            httpPut.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));
            break;
        default:
            throw new IllegalStateException("" + method);
        }
        HttpResponse response;
        try {
            String reponseId = UUID.randomUUID().toString();
            response = httpClient.execute(httpMethod, httpContext);
            response.setHeader("responseId", reponseId);
            LOG.debug("response status line: " + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {

                responseContents.put(reponseId, getHttpResponseContent(response));
                //lastResponseContent = getHttpResponseContent(response);
            } else {
                lastResponseContent = null;
            }
            httpMethod.reset();
        } catch (IOException e) {
            LOG.info("not done response");
            throw new IllegalStateException(e);
        }

        return response;
    }

    /**
     * log in the current http client session
     * @param userName
     * @param password
     */
    public void login(String userName, String password) {
        LOG.debug("doing login with: " + userName + " " + password);
        HttpPost httpPost = new HttpPost(getUrl("/j_spring_security_check"));
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("j_username", userName));
        nameValuePairs.add(new BasicNameValuePair("j_password", password));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("login failed because of encoding: " + e.getMessage());
        }
        HttpResponse response;
        try {
            response = httpClient.execute(httpPost, httpContext);
            httpPost.reset();
        } catch (IOException e) {
            LOG.info("not done response");
            throw new IllegalStateException(e);
        }
        if (response.getStatusLine().getStatusCode() != 302) {
            fail("login failed");
        }
        for (Header header : response.getAllHeaders()) {
            LOG.debug(header.getName() + " -> " + header.getValue());
            if (header.getName().equals("Location") && header.getValue().contains("error=true")) {
                fail("login failed illegal credentials");
            }
        }
        LOG.info("login success, response: " + response);
    }

    /**
     * logout
     */
    public void logout() {
        HttpResponse response = getResponse(Method.GET, getUrl("/j_spring_security_logout"));
        responseContents.clear();
        LOG.info("logout: " + response);
    }

    /**
     * the base application url in the form: "http://localhost:8080/oskar"
     *
     * @param path the path to the url starting with "/"
     * @return the baseUrl
     */
    public static String getUrl(String path) {
        return baseUrl + path;
    }

    /**
     * checks if given expected content is found in the http response header with given key
     * @param httpResponse
     * @param headerKey
     * @param expectedContents
     * @return
     */
    public static boolean checkHeaderContent(HttpResponse httpResponse, String headerKey, String... expectedContents) {
        Header[] headers = httpResponse.getHeaders(headerKey);
        if (headers == null || headers.length != 1) {
            LOG.error("given header " + headerKey + " not found");
            return false;
        }
        if (expectedContents == null || expectedContents.length == 0) {
            return true;
        }
        Header header = headers[0];
        Set<String> values = new HashSet<String>();
        values.addAll(Arrays.asList(header.getValue().split(",")));
        for (String expectedContent : expectedContents) {
            LOG.debug("values: " + values + " -> expectedContent: " + expectedContent);
            if (values.contains(expectedContent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * response content of the last call, is null if response was not 2xx
     * @return
     */
    @Deprecated
    public String getLastResponseContent() {
        return lastResponseContent;
    }

    /**
     * returns the content found for given responseId
     * @param responseId
     * @return
     */
    public String getContentForResponse(HttpResponse httpResponse) {
        return responseContents.get(httpResponse.getHeaders("responseId")[0].getValue());
    }

    /**
     * returns the http content
     * @param httpResponse
     * @return
     */
    private static String getHttpResponseContent(HttpResponse httpResponse) {
        BasicResponseHandler brh = new BasicResponseHandler();
        try {
            String response = brh.handleResponse(httpResponse);
            return response;
        } catch (HttpResponseException e) {
            throw new IllegalStateException("content not fetchable, msg.: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("content not fetchable, msg.: " + e.getMessage(), e);
        }
    }

}



