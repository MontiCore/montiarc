/* (c) https://github.com/MontiCore/monticore */

/*
 * Valid model.
 */
component Composed {
  InComp ic("foo");
  OutComp oc;

  oc.outPort -> ic.inPort;
}