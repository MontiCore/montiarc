/* (c) https://github.com/MontiCore/monticore */

package generics;
import generics.TriGenericComponent;
/*
 * Valid model.
 */
component TriGenericInstantiation {
  TriGenericComponent<Map<String, Integer>, Double, String> comp;
}
