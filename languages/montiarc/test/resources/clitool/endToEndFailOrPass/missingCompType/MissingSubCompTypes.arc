/* (c) https://github.com/MontiCore/monticore */
package missingCompType;

/*
 * Invalid model. Types of subcomponents missing.
 */
component MissingSubCompTypes {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}
