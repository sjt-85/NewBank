package newbank.server;

public class CustomerID {
  private String key;
  private boolean loggedIn;

  public CustomerID(String key) {
    this.key = key;
    loggedIn = true;
  }

  public String getKey() {
    return key;
  }

  // Return whether customer is logged in
  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void logOut() {
    loggedIn = false;
  }
}
