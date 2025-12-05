/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The component directly extends itself and the inner components
 * Inner1 and Inner2 indirectly extend themselves (cyclic inheritance).
 */
component CircularInheritanceWithComposition1 extends CircularInheritanceWithComposition1 {

  port in int i1, i2;
  port out int o1, o2;

  component Inner1 extends Inner2 {
    port in int i1;
    port out int o1;
  }

  component Inner2 extends Inner1 {
    port in int i2;
    port out int o2;
  }

  Inner1 sub1;
  Inner2 sub2;

  i1 -> sub1.i1;
  i2 -> sub1.i2;

  sub1.o1 -> sub2.i1;
  sub1.o2 -> sub2.i2;

  sub2.o1 -> o1;
  sub2.o2 -> o2;

}
