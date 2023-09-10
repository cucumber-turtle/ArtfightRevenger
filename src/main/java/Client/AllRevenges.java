package Client;

import Client.Http.Login;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * Client requesting page data from artfight.net to find all "revenges" to be completed.
 *
 * @author cucumber 2022
 */
public class AllRevenges {
  /**
   * Used <a href="https://www.tutorialspoint.com/apache_httpclient/apache_httpclient_form_based_login.htm">...</a>
   *
   * @param args Executable arguments.
   */
  public static void main(String[] args) {
    Login loggedInClient = new Login();
    CloseableHttpClient httpClient = loggedInClient.login();
    System.out.println("Logged in to artfight.net");
    String username = loggedInClient.getUsername();
    System.out.println("Starting to get revenges for " + username + "...");
    Scrape.getRevenges(username, httpClient);
    System.out.println("Finished!");
  }
}