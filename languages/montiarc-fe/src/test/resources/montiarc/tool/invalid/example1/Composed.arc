/* (c) https://github.com/MontiCore/monticore */
package invalid.example1;

/*
 * Invalid model. Types of subcomponents missing.
 */
component Composed {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}