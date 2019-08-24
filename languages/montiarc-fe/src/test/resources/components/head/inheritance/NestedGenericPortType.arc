/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/*
 * Valid model.
 */
component NestedGenericPortType<T> {
  port in List<Map<String, T>> nestedGenericInPort;
  port out List<Map<String, T>> nestedGenericOutPort;
}
