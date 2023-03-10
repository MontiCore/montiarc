/* (c) https://github.com/MontiCore/monticore */
package a.b;

public class JavaMill {

  public static JavaLib lib() {
    return new JavaLib();
  }

  public static JavaLib lib(int a, int b, int c) {
    return new JavaLib(a, b, c);
  }
}
