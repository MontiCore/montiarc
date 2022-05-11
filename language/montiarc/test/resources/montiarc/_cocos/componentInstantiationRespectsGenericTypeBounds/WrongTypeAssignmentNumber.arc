/* (c) https://github.com/MontiCore/monticore */
package componentInstantiationRespectsGenericTypeBounds;

/*
 * Invalid model.
 * (Let the following have one missing type parameter and one too many)
 */
component WrongTypeAssignmentNumber {
  CompWithoutTypeArgs withoutTypeArgs;

  GenericComp<
    int,          // Not Bounded
    boolean,      // Not Bounded
    Person,       // Not Bounded
    List<Student>, // Not Bounded

    int,     // Bound: int
    int,     // Bound: int
    boolean, // Bound: boolean
    boolean, // Bound: boolean

    Person,  // Bound: Person
    Person,  // Bound: Person
    Student,  // Bound: Student
    Student, // Bound: Student

    List<Person>,       // Bound: List<Person>
    List<Person>,       // Bound: List<Person>
    GlassStudent, // Bounds: Student & Comparable<Student>
    GlassStudent  // Bounds: Student & Comparable<Student>
    // Missing    // Bounds: Student & Comparable<Student>
  > myInstance;

  GenericComp<
      int,          // Not Bounded
      boolean,      // Not Bounded
      Person,       // Not Bounded
      List<Student>, // Not Bounded

      int,     // Bound: int
      int,     // Bound: int
      boolean, // Bound: boolean
      boolean, // Bound: boolean

      Person,  // Bound: Person
      Person,  // Bound: Person
      Student,  // Bound: Student
      Student, // Bound: Student

      List<Person>,       // Bound: List<Person>
      List<Person>,       // Bound: List<Person>
      GlassStudent, // Bounds: Student & Comparable<Student>
      GlassStudent, // Bounds: Student & Comparable<Student>
      GlassStudent, // Bounds: Student & Comparable<Student>
      GlassStudent  // Bounds: None (Too many arguments)
    > myInstance2;
}