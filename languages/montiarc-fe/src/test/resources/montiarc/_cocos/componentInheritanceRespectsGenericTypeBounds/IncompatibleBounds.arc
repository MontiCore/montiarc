/* (c) https://github.com/MontiCore/monticore */
package componentInheritanceRespectsGenericTypeBounds;

import componentInheritanceRespectsGenericTypeBounds.parents.*;

/*
 * Invalid model.
 * (Let the following types be resolvable: int, double, boolean, TruthNumber extends int & boolean, Fish, Person,
 * Student extends Person, List<T>, Comparable<V>)
 */
component IncompatibleBounds extends GenericComp<
  int, // Not Bounded
  int, // Not Bounded
  int, // Not Bounded
  int, // Not Bounded

  double, // Bound: int
  Person, // Bound: int
  double, // Bound: boolean
  Person, // Bound: boolean

  double,       // Bound: Person
  List<Person>, // Bound: Person
  double,       // Bound: Student
  Person,       // Bound: Student

  List<Student>,      // Bound: List<Person>
  Comparable<Person>, // Bound: List<Person>

  Fish,               // Bounds: Student & Comparable<Student>
  Student,            // Bounds: Student & Comparable<Student>
  Comparable<Student> // Bounds: Student & Comparable<Student>
> { }