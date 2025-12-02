/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing of subcomponent sub is missing (the component
 * cannot be resolved). Therefore, the ports sub.i and sub.o cannot be resolved.
 */
component MissingSymbolsInConnector1 {

  port in int i;
  port out int o;

  Missing sub;

  i -> sub.i;
  sub.o -> o;

  sub.o2 -> sub.i2;

}
