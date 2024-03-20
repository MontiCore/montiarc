/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of both ports of the inner component cannot
 * be resolved. The tool should report an error for the missing types.
 */
component MissingPortType4 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out Missing o;
  }
}
