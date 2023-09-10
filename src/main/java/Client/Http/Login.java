package main.java.Client.Http;

import main.java.Utils.Authenticate;
import main.java.Utils.ParseJson;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Login {
    private String username;

    public CloseableHttpClient login() {
        // Http client using standard cookie specification
        /* On http client redirects: https://www.baeldung.com/httpclient-redirect-on-http-post */
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setCookieSpec(StandardCookieSpec.STRICT).build()).build();
        ClassicHttpRequest httpPost = buildRequest();
        try {
            // First request is to log in to the site
            httpClient.execute(httpPost, response -> {
                // Client is redirected and status should be OK
                assertEquals(HttpStatus.SC_OK, response.getCode());
                return null;
            });
            return httpClient;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    /**
     * Build the initial request to server with authentication information.
     *
     * @return HttpUriRequest.
     */
    private ClassicHttpRequest buildRequest() {
        // Get authenticate file path and parse to Authenticate object
        Authenticate auth = ParseJson.readAuthenticateFile("src/main/resources/authenticate.json");
        assertNotNull(auth);
        this.username = auth.getUsername();
        String password = auth.getPassword();

        // Build post request for login
        ClassicRequestBuilder reqbuilder = ClassicRequestBuilder.post();
        //Set URI and parameters
        reqbuilder = reqbuilder.setUri("https://artfight.net/login");
        reqbuilder = reqbuilder.addParameter("username", this.username).addParameter("password", password);

        return reqbuilder.build();
    }
}
