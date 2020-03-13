package newbank.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// Taken from:
// https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
// Currently unsecure would need to add salt
public class HashGenerator {
  public String generateHash(String passwordToHash) {
    String generatedPassword = null;
    try {
      // Create MessageDigest instance for MD5
      MessageDigest md = MessageDigest.getInstance("MD5");
      // Add password bytes to digest
      md.update(passwordToHash.getBytes());
      // Get the hash's bytes
      byte[] bytes = md.digest();
      // This bytes[] has bytes in decimal format;
      // Convert it to hexadecimal format
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      // Get complete hashed password in hex format
      generatedPassword = sb.toString();
      return generatedPassword;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }
}
