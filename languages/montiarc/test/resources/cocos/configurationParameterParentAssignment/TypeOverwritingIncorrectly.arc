/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment;

import configurationParameterParentAssignment.superComponents.*;

/**
 * Invalid model. (Let the given types be resolvable).
 */
component TypeOverwritingIncorrectly<A> (
  Person strOverwritten,  // should be type 'String'
  int boolOverwritten,    // should be type 'boolean'
  boolean integOverwritten,  // should be type 'int'
  Person studentOverwritten, // should be type 'Person'
  Person person,
  int tOverwritten,  // should be type 'A'
  A uOverwritten,    // should be type 'int'
  List<boolean> intListOverwritten,  // should be type 'List<int>'
  List<Person> personList,
  List<boolean> vListOverwritten    // should be type 'List<int>'
) extends WithVariousTypesAsParameters<A, int, int> (
  strOverwritten,
  boolOverwritten,
  integOverwritten,
  studentOverwritten,
  person,
  tOverwritten,
  uOverwritten,
  intListOverwritten,
  personList,
  vListOverwritten
){ }
