/* (c) https://github.com/MontiCore/monticore */
package configurationParametersCorrectlyInherited.superComponents;

/**
 * Valid model. (Let all given types be resolvable).
 */
component WithVariousTypesAsParameters<T, U, V> (
  String str,
  boolean bool,
  int integ,
  Student student,
  Person person,
  T t,
  U u,
  List<int> intList,
  List<Person> personList,
  List<V> vList
) { }