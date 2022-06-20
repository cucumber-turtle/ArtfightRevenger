import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * Client requesting page data from artfight.net to find all "revenges" to be completed.
 *
 * @author cucumber 2022
 */
public class AllRevenges {
  private static final String SITE_URL = "https://artfight.net";

  /**
   * Used https://www.tutorialspoint.com/apache_httpclient/apache_httpclient_form_based_login.htm
   *
   * @param args Executable arguments.
   */
  public static void main(String[] args) {
    // Get authenticate file path and parse to Authenticate object
    java.net.URL resourceUrl = AllRevenges.class.getResource("authenticate.json");
    assertNotNull(resourceUrl);
    Authenticate auth = ParseJson.readAuthenticateFile(resourceUrl.getPath());
    assertNotNull(auth);
    String username = auth.getUsername();
    String password = auth.getPassword();

    // Build post request for login
    RequestBuilder reqbuilder = RequestBuilder.post();
    //Set URI and parameters
    reqbuilder = reqbuilder.setUri(SITE_URL + "/login");
    reqbuilder = reqbuilder.addParameter("username", username).addParameter("password", password);

    // Http client using standard cookie specification
    /* On http client redirects: https://www.baeldung.com/httpclient-redirect-on-http-post */
    HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).build()).setRedirectStrategy(new LaxRedirectStrategy())
        .build();
    HttpUriRequest httpPost = reqbuilder.build();
    try {
      HttpResponse httpresponse = httpClient.execute(httpPost);
      // Client is redirected and status should be OK
      assertEquals(HttpStatus.SC_OK, httpresponse.getStatusLine().getStatusCode());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Accessing characters page
    HttpGet request = new HttpGet(SITE_URL + "/manage/characters");
    HttpResponse response;
    try {
      response = httpClient.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();

      assertEquals(HttpStatus.SC_OK, statusCode);
      response.getEntity();
      HttpEntity entity = response.getEntity();
      if (entity == null) {
        System.out.println("Null content.");
      } else {
        // Writing page content to file for manual testing
        InputStream stream = entity.getContent();
        FileOutputStream output = new FileOutputStream("test-page.html");
        output.write(stream.readAllBytes());
        stream.close();
        output.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
