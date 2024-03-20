/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the incoming port of the inner component
 * cannot be resolved. The port is used as the target of a port forward. The
 * tool should report an error for the missing type and an error for the
 * mismatching types.
 */
component MissingPortType7 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
