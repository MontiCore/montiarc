/* (c) https://github.com/MontiCore/monticore */
package configurationParameterParentAssignment.superComponents;

import java.lang.String;
import java.util.List;

/**
 * Valid model.
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
