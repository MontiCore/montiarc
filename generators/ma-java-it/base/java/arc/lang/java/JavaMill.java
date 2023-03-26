/* (c) https://github.com/MontiCore/monticore */
package arc.lang.java;

public class JavaMill {

  public static JavaLib lib() {
    return new JavaLib();
  }

  public static JavaLib lib(int a, int b, int c) {
    return new JavaLib(a, b, c);
  }
}
