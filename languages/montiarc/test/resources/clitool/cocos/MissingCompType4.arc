/* (c) https://github.com/MontiCore/monticore */
component MissingCompType4 {

  port in int i;
  port out int o;

  Missing sub;

  i -> sub.i;
  sub.o -> o;

  sub.o2 -> sub.i2;

}
