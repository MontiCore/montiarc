/* (c) https://github.com/MontiCore/monticore */
package ooTypesFromSymFiles;

/*
 * Valid model.
 */
component Composed {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}
