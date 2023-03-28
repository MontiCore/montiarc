/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import configurationParameterParentAssignment.superComponents.*;
import java.lang.String;
import java.util.List;

/**
 * Valid model. (Let all given types be resolvable and Student be a sub type of Person)
 */
component TypeOverwritingWithoutChange<A, B, C> (
  String str,
  boolean bool,
  int integ,
  Student student,
  Person person,
  A tOverwritten,
  B bOverwritten,
  List<int> intList,
  List<Person> personList,
  List<C> vListOverwritten
) extends WithVariousTypesAsParameters<A, B, C> (
  str,
  bool,
  integ,
  student,
  person,
  tOverwritten,
  bOverwritten,
  intList,
  personList,
  vListOverwritten
) { }
