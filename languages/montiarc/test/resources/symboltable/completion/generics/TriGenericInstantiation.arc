/* (c) https://github.com/MontiCore/monticore */
package completion.generics;

import generics.TriGenericComponent;
/*
 * Valid model.
 */
component TriGenericInstantiation {
  TriGenericComponent<Map<String, Integer>, Double, String> comp;
}
