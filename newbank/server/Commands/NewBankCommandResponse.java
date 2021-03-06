package newbank.server.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class NewBankCommandResponse {
  private ResponseType type;
  private String description;
  private BufferedReader in;
  private PrintWriter out;

  public NewBankCommandResponse() {}

  public NewBankCommandResponse(BufferedReader in, PrintWriter out) {
    setStream(in, out);
  }

  public enum ResponseType {
    EMPTY,
    SUCCEEDED,
    FAILED,
    INVALIDREQUEST,
    HELP,
    VIEWED
  }

  public static final NewBankCommandResponse EMPTY =
      new NewBankCommandResponse().setState(ResponseType.EMPTY, "");

  public static NewBankCommandResponse createSucceeded(String description) {
    return new NewBankCommandResponse().succeeded(description);
  }

  public static NewBankCommandResponse createViewed(String description) {
    return new NewBankCommandResponse().viewed(description);
  }

  public static NewBankCommandResponse createFailed(String description) {
    return new NewBankCommandResponse().failed(description);
  }

  public static NewBankCommandResponse createInvalidRequest(String description) {
    return new NewBankCommandResponse().invalidRequest(description);
  }

  public static NewBankCommandResponse createHelp() {
    return new NewBankCommandResponse().help("");
  }

  public NewBankCommandResponse succeeded(String description) {
    return setState(ResponseType.SUCCEEDED, "SUCCESS: " + description);
  }

  public NewBankCommandResponse viewed(String description) {
    return setState(ResponseType.VIEWED, description);
  }

  public NewBankCommandResponse failed(String description) {
    return setState(ResponseType.FAILED, "FAIL: " + description);
  }

  public NewBankCommandResponse invalidRequest(String description) {
    return setState(ResponseType.INVALIDREQUEST, "FAIL: " + description);
  }

  public NewBankCommandResponse help(String description) {
    return setState(ResponseType.HELP, description);
  }

  public String query(String message) {
    try {
      out.println(message + " :");
      String line = in.readLine();
      return line == null ? "" : line; // When called by test, this can be null.
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  public boolean confirm(String message) {
    do {
      switch (query(message + " [Y]es/[N]o").toUpperCase()) {
        case "Y":
          return true;
        case "N":
          return false;
      }
    } while (true);
  }

  private NewBankCommandResponse setState(ResponseType type, String description) {
    this.type = type;
    this.description = description;
    return this;
  }

  public void setStream(BufferedReader in, PrintWriter out) {
    this.in = in;
    this.out = out;
  }

  public ResponseType getType() {
    return this.type;
  }

  public String getDescription() {
    return description;
  }
}
