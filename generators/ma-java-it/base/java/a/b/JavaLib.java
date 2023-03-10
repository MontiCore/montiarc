/* (c) https://github.com/MontiCore/monticore */
package a.b;

@SuppressWarnings("unused")
public class JavaLib {

  public int var1;
  protected int var2;
  private int var3;

  public static int svar1 = 4;
  protected static int svar2 = 5;
  private static int svar3 = 6;

  public JavaLib() {
    this.var1 = 1;
    this.var2 = 2;
    this.var3 = 3;
  }

  public JavaLib(int a, int b, int c) {
    this.var1 = a;
  }

  public int getVar1() {
    return this.var1;
  }

  protected int getVar2() {
    return this.var2;
  }

  private int getVar3() {
    return this.var3;
  }

  public void setVar1(int var1) {
    this.var1 = var1;
  }

  protected void setVar2(int var2) {
    this.var2 = var2;
  }

  private void setVar3(int var3) {
    this.var3 = var3;
  }

  public static int getSvar1() {
    return svar1;
  }

  public static void setSvar1(int _d) {
    svar1 = _d;
  }

  protected static int getSvar2() {
    return svar2;
  }

  protected static void setSvar2(int _e) {
    svar2 = _e;
  }

  private static int getSvar3() {
    return svar3;
  }

  private static void setSvar3(int _f) {
    svar3 = _f;
  }
}
