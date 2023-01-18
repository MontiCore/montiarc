/* (c) https://github.com/MontiCore/monticore */
package componentInheritanceRespectsGenericTypeBounds;

import componentInheritanceRespectsGenericTypeBounds.parents.*;

/*
 * Valid model.
 * (Let the following types be resolvable: int, double, boolean, Person, Student extends Person,
 * GlassStudent extends Student & Comparable<Student>, List<T>, LinkedList<U> extends List<U>, Comparable<V>)
 */
component CompatibleBounds extends GenericComp<
  int,          // Not Bounded
  boolean,      // Not Bounded
  Person,       // Not Bounded
  List<Student>, // Not Bounded

  int,     // Bound: int
  int,     // Bound: int
  boolean, // Bound: boolean
  boolean, // Bound: boolean

  Person,  // Bound: Person
  Student,  // Bound: Person
  Student,  // Bound: Student
  Student, // Bound: Student

  List<Person>,       // Bound: List<Person>
  List<Person>,       // Bound: List<Person>. THIS LINE IS TO BE REPLACED (see below)
  /* TODO: Currently LinkedList<Person> and List<Person> are incompatible due to some bug in MontiCore.
   * When fixed, replace the preceding line with the following new line.
   * LinkedList<Person>, // Bound: List<Person>
   */
  GlassStudent, // Bounds: Student & Comparable<Student>
  GlassStudent, // Bounds: Student & Comparable<Student>
  GlassStudent  // Bounds: Student & Comparable<Student>
> { }
