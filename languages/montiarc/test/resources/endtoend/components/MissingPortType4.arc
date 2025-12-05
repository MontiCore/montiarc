/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The type Missing (simple name) of ports i and o of inner
 * component Inner is missing (the datatype cannot be resolved).
 */
component MissingPortType4 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out Missing o;
  }
}
