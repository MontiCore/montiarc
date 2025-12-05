/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Valid model, where the types external CircularInheritanceWithComposition3B
 * and CircularInheritanceWithComposition3C, which are resolved via a serialized
 * symbol table and are instantiated as subcomponents, circularly extend themselves.
 */
component CircularInheritanceWithComposition3A {

  port in int i1, i2;
  port out int o1, o2;

  CircularInheritanceWithComposition3B sub1;
  CircularInheritanceWithComposition3C sub2;

  i1 -> sub1.i1;
  i2 -> sub1.i2;

  sub1.o1 -> sub2.i1;
  sub1.o2 -> sub2.i2;

  sub2.o1 -> o1;
  sub2.o2 -> o2;

}
