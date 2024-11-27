/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

/**
 * Assertion library providing common assert methods.
 */
public class Assertions extends AssertionsTOP {

  public static void fail() {
    org.junit.jupiter.api.Assertions.fail();
  }

  public static void fail(String message) {
    org.junit.jupiter.api.Assertions.fail(message);
  }

  public static void assertTrue(boolean expression) {
    org.junit.jupiter.api.Assertions.assertTrue(expression);
  }

  public static void assertTrue(boolean expression, String message) {
    org.junit.jupiter.api.Assertions.assertTrue(expression, message);
  }

  public static void assertFalse(boolean expression) {
    org.junit.jupiter.api.Assertions.assertFalse(expression);
  }

  public static void assertFalse(boolean expression, String message) {
    org.junit.jupiter.api.Assertions.assertFalse(expression, message);
  }

  public static void assertEquals(int expected, int actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(int expected, int actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertEquals(long expected, long actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(long expected, long actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertEquals(float expected, float actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(float expected, float actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertEquals(double expected, double actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(double expected, double actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertEquals(char expected, char actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(char expected, char actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertEquals(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  public static void assertEquals(Object expected, Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }

  public static void assertNotEquals(int expected, int actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(int expected, int actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }

  public static void assertNotEquals(long expected, long actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(long expected, long actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }

  public static void assertNotEquals(float expected, float actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(float expected, float actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }

  public static void assertNotEquals(double expected, double actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(double expected, double actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }

  public static void assertNotEquals(char expected, char actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(char expected, char actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }

  public static void assertNotEquals(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual);
  }

  public static void assertNotEquals(Object expected, Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotEquals(expected, actual, message);
  }
}
