/* (c) https://github.com/MontiCore/monticore */

/**
 * Valid model.
 */
component ParameterShadowing(int a, boolean b) {
  int s = a;
  boolean t = b;

  component Inner1(boolean a, int c) {
    boolean v = a;
    int w = c;

    component Inner2(int a, boolean b, int c) {
      int x = a;
      boolean y = b;
      int z = c;
    }
  }
}