/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

import configurationParametersCorrectlyInherited.superComponents.*;

/**
 * Valid model. (Let all the given types be resolvable.
 */
component TypeOverwritingCorrectly<A> (
  String str,
  boolean bool,
  double integOverwritten,
  Person personOverwritten,
  Person person,
  A a,
  Person uOverwritten,
  List<int> intList,
  List<Person> personList,
  List<Person> vListOverwritten
) extends WithVariousTypesAsParameters<A, Person, Person> { }