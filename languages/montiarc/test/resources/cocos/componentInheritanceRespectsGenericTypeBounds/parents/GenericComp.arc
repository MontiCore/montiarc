/* (c) https://github.com/MontiCore/monticore */
package componentInheritanceRespectsGenericTypeBounds.parents;

/*
 * Valid model.
 * (Let the following types be resolvable: int, boolean, Person, Student extends Person, List<T>, Comparable<U>)
 */
component GenericComp <
  NotBounded1,
  NotBounded2,
  NotBounded3,
  NotBounded4,

  BoundedByInt1 extends int,
  BoundedByInt2 extends int,
  BoundedByBool1 extends boolean,
  BoundedByBool2 extends boolean,

  BoundedByPerson1 extends Person,
  BoundedByPerson2 extends Person,
  BoundedByStudent1 extends Student,
  BoundedByStudent2 extends Student,

  BoundedByPersonList1 extends List<Person>,
  BoundedByPersonList2 extends List<Person>,

  BoundedByStudentAndComparable1 extends Student & Comparable<Student>,
  BoundedByStudentAndComparable2 extends Student & Comparable<Student>,
  BoundedByStudentAndComparable3 extends Student & Comparable<Student>
> { }
