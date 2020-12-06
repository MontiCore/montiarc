/* (c) https://github.com/MontiCore/monticore */

/*
 * Valid model.
 */
component Composed {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}