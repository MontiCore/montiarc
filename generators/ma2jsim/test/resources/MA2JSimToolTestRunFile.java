import org.junit.jupiter.api.Assertions;

public class MA2JSimToolTestRunFile {

  public static void main(String[] args) {
    Assertions.assertArrayEquals(new String[]{
      "-t", "test",
    }, args);
  }
}
