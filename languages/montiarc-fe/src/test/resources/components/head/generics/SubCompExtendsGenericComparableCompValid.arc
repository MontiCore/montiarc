/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model.
 * Generic type parameters with type bounds are currently prohibited. See #241 / #243
 */
component SubCompExtendsGenericComparableCompValid<K extends Comparable<K>> extends SuperGenericComparableComp<K> {

}