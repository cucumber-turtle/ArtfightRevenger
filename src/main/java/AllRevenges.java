import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
  private static String username;

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
    HttpUriRequest httpPost = buildRequest();
    try {
      HttpResponse httpresponse = httpClient.execute(httpPost);
      // Client is redirected and status should be OK
      assertEquals(HttpStatus.SC_OK, httpresponse.getStatusLine().getStatusCode());
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<AttackInfo> attacks = new ArrayList<>();
    try {
      String pageUrl = SITE_URL + "/~" + username + "/defenses";
      while (pageUrl != null) {
        // Getting character links for every page
        String document = requestGetHtml(httpClient, pageUrl);
        findAttacks(document, attacks);
        pageUrl = findNextPage(document);
      }
      for (AttackInfo attack : attacks) {
        String attackDoc = requestGetHtml(httpClient, attack.getAttackLink());
        AttackInfo info = getAttackInfo(attackDoc, attack);
        break;
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: for each atk: if !next atk-> save for revenging
   * @param html The document content of the http response.
   * @return An object with the attack information.
   */
  public static AttackInfo getAttackInfo (String html, AttackInfo attack) {
    Document doc = Jsoup.parse(html);
    Elements all = doc.getElementsByAttribute("href");
    // Find previous and next attack links if they exist and create an attackinfo object to return
    return null;
  }

  /**
   * Build the initial request to server with authentication information.
   *
   * @return HttpUriRequest.
   */
  private static HttpUriRequest buildRequest() {
    // Get authenticate file path and parse to Authenticate object
    java.net.URL resourceUrl = AllRevenges.class.getResource("authenticate.json");
    assertNotNull(resourceUrl);
    Authenticate auth = ParseJson.readAuthenticateFile(resourceUrl.getPath());
    assertNotNull(auth);
    username = auth.getUsername();
    String password = auth.getPassword();

    // Build post request for login
    RequestBuilder reqbuilder = RequestBuilder.post();
    //Set URI and parameters
    reqbuilder = reqbuilder.setUri("https://artfight.net/login");
    reqbuilder = reqbuilder.addParameter("username", username).addParameter("password", password);

    return reqbuilder.build();
  }

  /**
   * Send a http get request to the server and get the document content as a string.
   *
   * @param httpClient The client to execute the request.
   * @param url The url to send the request to.
   * @return The document content of the http response.
   * @throws IOException The request execution can throw an exception.
   */
  private static String requestGetHtml (HttpClient httpClient, String url)
      throws IOException, InterruptedException {
    HttpGet request = new HttpGet(url);
    HttpResponse response;
    // Always keep at least 2 seconds time between the last request and the next one
    Thread.sleep(2000);
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
        byte[] bytes = stream.readAllBytes();
        String document = new String (bytes, StandardCharsets.UTF_8);
        stream.close();
        try {
          // writing to file for testing
          FileOutputStream out = new FileOutputStream("test-page.html");
          out.write(bytes);
          out.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        return document;
      }
      throw new IOException ("Some error happened here.");
  }

  /**
   * Find html document elements containing valid character links and add to new list.
   *
   * @param html An HTML document to find attacks from.
   * @param attacks List to add attack info objects to.
   */
  private static void findAttacks (String html, List<AttackInfo> attacks) {
    Document doc = Jsoup.parse(html);
    Elements allLinks = doc.getElementsByAttribute("href");
    Pattern pattern =
        Pattern.compile("<a href=\"https://artfight.net/attack/[0-9]+(.|\n)+");
    for (Element e : allLinks) {
      if (pattern.matcher(e.toString()).matches()) {
        String[] splitElement = e.toString().split("\"");
        String attackLink = splitElement[1];
        String[] splitLink = attackLink.split("\\.");
        String attackName = splitLink[2];
        String[] splitAttackTitle = splitElement[13].split("by ");
        String attacker = splitAttackTitle[splitAttackTitle.length - 1];
        attacks.add(new AttackInfo(attackName, attacker, attackLink));
      }
    }
  }

  /**
   * Finds the next page.
   *
   * @param html An HTML document to find attacks from.
   * @return Returns null if no next page is found, otherwise, returns the URL of the next page.
   */
  private static String findNextPage (String html) {
    Document doc = Jsoup.parse(html);
    Elements nextPage = doc.getElementsByAttributeValue("rel", "next");
    for (Element e : nextPage) {
      if (Pattern.matches("<a class=\"page-link\" href=\"https://artfight.net/~(.|\n)+",
          e.toString())) {
        String[] splitElement = e.toString().split("\"");
        return splitElement[3];
      }
    }
    return null;
  }
}
