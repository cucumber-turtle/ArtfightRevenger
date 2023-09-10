package Client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Persistency.Record;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
   * Used <a href="https://www.tutorialspoint.com/apache_httpclient/apache_httpclient_form_based_login.htm">...</a>
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
      Record record = new Record("my-revenges");
      for (AttackInfo attack : attacks) {
        String attackDoc = requestGetHtml(httpClient, attack.getAttackLink());
        int otherAttacks = getAttackInfo(attackDoc, attack);
        if (otherAttacks != -1) {
          record.addRevenge(attack);
        }
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Find any related attacks to this one.
   * Only two relationships are considered, directly preceding, and directly following the attack.
   *
   * @param html The document content of the http response.
   * @return Return -1 if already revenged. 1 if it has a previous attack. 0 if no related attacks.
   */
  public static int getAttackInfo (String html, AttackInfo attack) {
    Document doc = Jsoup.parse(html);
    Elements next = doc.select("table:contains(next)");
    if (!next.isEmpty()) {
      // If an attack has a next attack then it has already been revenged
      return -1;
    }
    Elements prev = doc.select("table:contains(previous)");
    if (!prev.isEmpty()) {
      Elements elementsWithLink = prev.select("a[href~=https://artfight.net/attack/[0-9]+.+]");
      String[] splitElement = elementsWithLink.get(0).toString().split("\"");
      attack.setPrevAttack(splitElement[1]);
      return 1;
    }
    return 0;
  }

  /**
   * Build the initial request to server with authentication information.
   *
   * @return HttpUriRequest.
   */
  private static HttpUriRequest buildRequest() {
    // Get authenticate file path and parse to Authenticate object
    Authenticate auth = ParseJson.readAuthenticateFile("src/main/resources/authenticate.json");
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
   * Find html document elements containing valid attacks and add to existing list.
   *
   * @param html An HTML document to find attacks from.
   * @param attacks List to add attack info objects to.
   */
  private static void findAttacks (String html, List<AttackInfo> attacks) {
    Document doc = Jsoup.parse(html);
    Elements allLinks = doc.select("a[href~=https://artfight.net/attack/[0-9]+.+]");
    for (Element e : allLinks) {
      // Split by quotation marks to find attack link and title
      String[] splitElement = e.toString().split("\"");
      String attackLink = splitElement[1];
      // Split by "by" to find attacker name and attack title
      String[] splitAttackTitle = splitElement[13].split(" by ");
      String attacker = splitAttackTitle[splitAttackTitle.length - 1];
      String attackTitle = splitAttackTitle[splitAttackTitle.length - 2];
      // Create new AttackInfo object with the found information
      attacks.add(new AttackInfo(attackTitle, attacker, attackLink));
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
    Elements inNextElement = nextPage.select("a[href~=https://artfight.net/~.+]");
    if (!inNextElement.isEmpty()) {
      String[] splitElement = inNextElement.get(0).toString().split("\"");
      // Return link to next page
      return splitElement[3];
    }
    return null;
  }
}
