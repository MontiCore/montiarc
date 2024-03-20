/* (c) https://github.com/MontiCore/monticore */

/**
 * The model is invalid. The subcomponent's type cannot be resolved. The tool
 * should report an error for the missing type. However, the tool should end
 * gracefully even when referencing the subcomponent in a connector.
 */
component MissingCompType4 {

  port in int i;
  port out int o;

  Missing sub;

  i -> sub.i;
  sub.o -> o;

  sub.o2 -> sub.i2;

}
