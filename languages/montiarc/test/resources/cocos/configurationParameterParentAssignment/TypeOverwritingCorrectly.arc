/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import configurationParameterParentAssignment.superComponents.*;

/**
 * Valid model. (Let all the given types be resolvable and Student be a sub type of Person).
 */
component TypeOverwritingCorrectly<A> (
  String str,
  boolean bool,
  int integ,
  Student student,
  Student personOverwritten,
  A a,
  Person uOverwritten,
  List<int> intList,
  List<Person> personList,
  List<Person> vListOverwritten
) extends WithVariousTypesAsParameters<A, Person, Person> (
  str,
  bool,
  integ,
  student,
  personOverwritten,
  a,
  uOverwritten,
  intList,
  personList,
  vListOverwritten
) { }
