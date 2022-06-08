/* (c) https://github.com/MontiCore/monticore */
package fieldTypeExists;

/**
 * Valid model. (As long as List<T>, Foo<U>, qual.Typee, qual.Generic, and Person are resolvable. Permit null)
 */
component HasValidTypes<T> {

  T p1 = null;
  int p2 = null;
  Person p3 = null;
  List<Person> p4 = null;
  Foo<Person> p5 = null;
  qual.Typee p6 = null;
  qual.Generic<Person> p7 = null;
}