/* (c) https://github.com/MontiCore/monticore */
package connectedSubComponents;

/*
 * Valid model.
 */
component Composed {
  InComp ic(14);
  OutComp oc;

  oc.outPort -> ic.inPort;
}
