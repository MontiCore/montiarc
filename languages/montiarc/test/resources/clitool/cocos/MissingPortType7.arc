/* (c) https://github.com/MontiCore/monticore */
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
