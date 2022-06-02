/* (c) https://github.com/MontiCore/monticore */
package parameterTypeExists;

/**
 * Valid model. (As long as List<T>, Foo<U>, qual.Typee, qual.Generic, and Person are resolvable. Permit null)
 */
component HasValidTypes<T> (
  T p1,
  int p2,
  Person p3,
  List<Person> p4,
  Foo<Person> p5,
  qual.Typee p6 = null,
  qual.Generic<Person> p7 = null
) { }