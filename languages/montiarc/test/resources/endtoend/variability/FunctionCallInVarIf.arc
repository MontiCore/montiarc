/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * The varif condition depends on the result of a method call that is not known
 * at compile time.
 */
component FunctionCallInVarIf {
  port out int o;

  component Inner {
    port out int o;
  }

  Inner a, b;

  a.o -> o;

  varif (OOTypeWithStaticBooleanFunction.m()) {
    b.o -> o;
  }
}
