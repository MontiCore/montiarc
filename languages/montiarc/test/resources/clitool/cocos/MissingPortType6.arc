/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the outgoing port cannot be resolved.
 * The port is used as the target of a port forward. The tool should report an
 * error for the missing type and an error for the mismatching types.
 */
component MissingPortType6 {

  port in int i;
  port out Missing o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
