package test;

import main.java.Client.Http.Login;
import main.java.Client.Scrape;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestScrape {
    private static final String SITE_URL = "https://artfight.net";

    @Test
    public void testScrape1() {
        // Test to make sure that the scrape function works
        Login login = new Login();
        CloseableHttpClient client = login.login();
        Scrape.getRevenges(login.getUsername(), client);
    }

    @Test
    public void testScrape2() {
        Login login = new Login();
        CloseableHttpClient client = login.login();
        String username = login.getUsername();
        String pageUrl = SITE_URL + "/~" + username + "/defenses";
        try {
            String document = Client.Http.RequestHtml.requestGetHtml(client, pageUrl);
            Document doc = Jsoup.parse(document);
            Elements allLinks = doc.select("a[href~=https://artfight.net/attack/[0-9]+.+]");
            assertNotEquals(0, allLinks.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testScrape3() {
        Login login = new Login();
        CloseableHttpClient client = login.login();
        try {
            String document = Client.Http.RequestHtml.requestGetHtml(client, SITE_URL);
            Document doc = Jsoup.parse(document);
            Elements allLinks = doc.select("a[href~=https://artfight.net/attack/[0-9]+.+]");
            assertEquals(0, allLinks.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
