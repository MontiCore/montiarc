/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited;

import configurationParametersCorrectlyInherited.superComponents.*;

/**
 * Invalid model. (Let the given types be resolvable).
 */
component TypeOverwritingIncorrectly<A> (
  Person strOverwritten,  // should be type 'String'
  int boolOverwritten,    // should be type 'boolean'
  boolean integOverwritten,  // should be type 'int'
  Student student,
  Student personOverwritten, // should be type 'Person'
  int tOverwritten,  // should be type 'A'
  A uOverwritten,    // should be type 'int'
  List<boolean> intListOverwritten,  // should be type 'List<int>'
  List<Person> personList,
  List<boolean> vListOverwritten    // should be type 'List<int>'
) extends WithVariousTypesAsParameters<A, int, int> { }