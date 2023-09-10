package main.java.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 * Simple class for parsing a json file to an object storing authentication information.
 * @see Authenticate
 *
 * @author cucumber 2022
 */
public class ParseJson {
  public static Authenticate readAuthenticateFile (String filename) {
    // Parse to Authenticate object
    ObjectMapper mapper = new ObjectMapper();
    Authenticate authDetails;
    try {
      authDetails = mapper.readValue(new File(filename), Authenticate.class);
      return authDetails;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
