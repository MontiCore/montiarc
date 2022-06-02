/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

import configurationParametersCorrectlyInherited.superComponents.*;

/**
 * Valid model. (Let all given types be resolvable and Student be a sub type of person)
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
) extends WithVariousTypesAsParameters<A, B, C> { }