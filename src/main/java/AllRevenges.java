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
    // Http client using standard cookie specification
    /* On http client redirects: https://www.baeldung.com/httpclient-redirect-on-http-post */
    HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).build()).setRedirectStrategy(new LaxRedirectStrategy())
        .build();
    HttpUriRequest httpPost = buildRequest(SITE_URL + "/login");
    try {
      HttpResponse httpresponse = httpClient.execute(httpPost);
      // Client is redirected and status should be OK
      assertEquals(HttpStatus.SC_OK, httpresponse.getStatusLine().getStatusCode());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Accessing characters page
    try {
      String document = requestGetHtml(httpClient, SITE_URL + "/manage/characters");
      List<String> characterLinks = findCharacters(document);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static HttpUriRequest buildRequest(String url) {
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
    reqbuilder = reqbuilder.setUri(url);
    reqbuilder = reqbuilder.addParameter("username", username).addParameter("password", password);

    return reqbuilder.build();
  }

  private static String requestGetHtml (HttpClient httpClient, String url) throws IOException {
    HttpGet request = new HttpGet(url);
    HttpResponse response;
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
        stream.close();
        return document;
      }
      throw new IOException ("Some error happened here.");
  }

  private static List<String> findCharacters (String html) {
    Document doc = Jsoup.parse(html);
    Elements allLinks = doc.getElementsByAttribute("href");
    List<String> characterList = new ArrayList<>();
    Pattern pattern =
        Pattern.compile("<a href=\"https://artfight.net/character/[0-9]+\\.?.*\">" +
            "<img src=\"https://images.artfight.net/character/.+\" title=\".+\" class=\".*\"></a>");
    for (Element e : allLinks) {
      if (pattern.matcher(e.toString()).matches()) {
        String[] splitElement = e.toString().split("\"");
        System.out.println(splitElement[1]);
        characterList.add(splitElement[1]);
      }
    }
    return characterList;
  }
}
