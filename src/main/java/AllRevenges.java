import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        String document = new String (stream.readAllBytes(), StandardCharsets.UTF_8);
        findCharacters(document);
        stream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void findCharacters (String html) {
    Document doc = Jsoup.parse(html);
    Elements allLinks = doc.getElementsByAttribute("href");
    List<String> characterList = new ArrayList<>();
    // TODO: fix regex pattern not matching completely
    Pattern pattern =
        Pattern.compile("<a href=\"https://artfight.net/character/[0-9]+\\..+\">");
    for (Element e : allLinks) {
      System.out.println("Not matched yet: " + e);
      if (pattern.matcher(e.toString()).matches()) {
        System.out.println(e);
        characterList.add(e.toString());
      }
    }
  }
}
