import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple class for storing authentication information.
 *
 * @author cucumber 2022
 */
public class Authenticate {
  private final String username;
  private final String password;

  public Authenticate (@JsonProperty("username") String username,
                       @JsonProperty("password") String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
