package newbank.server.Commands;

public class NewBankCommandResponse {
  private final ResponseType type;
  private String description;

  public enum ResponseType {
    Succeeded,
    Failed,
    InvalidRequest
  }

  public static NewBankCommandResponse succeeded(String description) {
    return new NewBankCommandResponse(ResponseType.Succeeded, description);
  }

  protected NewBankCommandResponse(ResponseType type, String description) {
    this.type = type;
    this.description = description;
  }

  public ResponseType getType() {
    return this.type;
  }

  public String getDescription() {
    return description;
  }
}
