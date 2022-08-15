/* (c) https://github.com/MontiCore/monticore */
package completion.generics;

/*
 * Valid model.
 */
component TriGenericComponent<T, U extends Number, V> {
  port in T inPort;
}
