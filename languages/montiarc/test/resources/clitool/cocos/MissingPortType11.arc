/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The type of the incoming port of an inner component
 * and the type of the outgoing port of another inner component cannot be
 * resolved. The ports are used as source and target of a hidden connector.
 * The tool should report an error for the missing types but no type mismatch
 * error, as source and target are of the same (missing) type.
 */
component MissingPortType11 {

  port in int i;
  port out int o;

  component Inner1 {
    port in int i;
    port out Missing o;
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
