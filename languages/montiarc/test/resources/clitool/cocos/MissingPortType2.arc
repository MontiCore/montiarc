/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the outgoing port cannot be resolved.
 * The tool should report an error for the missing type.
 */
component MissingPortType2 {

  port in int i;
  port out Missing o;

}
