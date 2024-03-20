/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the outgoing port of the inner component
 * cannot be resolved. The port is used as the source of a port forward. The
 * tool should report an error for the missing type and an error for the
 * mismatching types.
 */
component MissingPortType8 {

  port in int i;
  port out int o;

  component Inner {
    port in int i;
    port out Missing o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
