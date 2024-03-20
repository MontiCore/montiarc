/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the incoming port of an inner component
 * cannot be resolved. The port is used as the target of a hidden connector.
 * The tool should report an error for the missing type and an error for the
 * mismatching types.
 */
component MissingPortType9 {

  port in int i;
  port out int o;

  component Inner1 {
    port in int i;
    port out int o;
  }

  component Inner2 {
    port in Missing i;
    port out int o;
  }

  Inner1 sub1;
  Inner2 sub2;

  i -> sub1.i;
  sub1.o -> sub2.i;
  sub2.o -> o;
}
