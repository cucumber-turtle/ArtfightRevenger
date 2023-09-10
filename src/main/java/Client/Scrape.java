package main.java.Client;

import Client.Http.RequestHtml;
import main.java.Persistency.Record;
import main.java.Utils.AttackInfo;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scrape {
    private static final String SITE_URL = "https://artfight.net";

    public static void getRevenges(String username, CloseableHttpClient httpClient) {
        // List of attacks
        List<AttackInfo> attacks = new ArrayList<>();
        int countRevenge = 0;
        try {
            String pageUrl = SITE_URL + "/~" + username + "/defenses";
            while (pageUrl != null) {
                // Getting character links for every page
                String document = RequestHtml.requestGetHtml(httpClient, pageUrl);
                findAttacks(document, attacks);
                pageUrl = findNextPage(document);
            }
            Record record = new Record("my-revenges");
            for (AttackInfo attack : attacks) {
                String attackDoc = RequestHtml.requestGetHtml(httpClient, attack.getAttackLink());
                int otherAttacks = getAttackInfo(attackDoc, attack);
                if (otherAttacks != -1) {
                    record.addRevenge(attack);
                    countRevenge += 1;
                }
            }
            System.out.println("Found a total of " + countRevenge + " attacks that can be revenged.");
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
