/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model.
 * Currently invalid due to #241, #243
 */
component ComponentExtendsGenericComponent2<K,V extends Number> extends GenericComp2<K, V> {
  // Empty body
}
