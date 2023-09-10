package Client.Http;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestHtml {
    /**
     * Send a http get request to the server and get the document content as a string.
     *
     * @param httpClient The client to execute the request.
     * @param url The url to send the request to.
     * @return The document content of the http response.
     * @throws IOException The request execution can throw an exception.
     */
    public static String requestGetHtml (CloseableHttpClient httpClient, String url)
            throws IOException, InterruptedException {
        HttpGet request = new HttpGet(url);
        // Always keep at least 2 seconds time between the last request and the next one
        Thread.sleep(2000);
        return httpClient.execute(request, response -> {
            // Client is redirected and status should be OK
            assertEquals(HttpStatus.SC_OK, response.getCode());
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
        });
    }
}
