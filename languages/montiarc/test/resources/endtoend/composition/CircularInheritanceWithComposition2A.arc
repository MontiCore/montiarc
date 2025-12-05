/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Valid model, where the types external CircularInheritanceWithComposition2B
 * and CircularInheritanceWithComposition2C, which are instantiated as
 * subcomponents, circularly extend themselves.
 */
component CircularInheritanceWithComposition2A {

  port in int i1, i2;
  port out int o1, o2;

  CircularInheritanceWithComposition2B sub1;
  CircularInheritanceWithComposition2C sub2;

  i1 -> sub1.i1;
  i2 -> sub1.i2;

  sub1.o1 -> sub2.i1;
  sub1.o2 -> sub2.i2;

  sub2.o1 -> o1;
  sub2.o2 -> o2;

}
