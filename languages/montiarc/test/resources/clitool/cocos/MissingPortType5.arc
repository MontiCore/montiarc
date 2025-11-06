/* (c) https://github.com/MontiCore/monticore */
component MissingPortType5 {

  port in Missing i;
  port out int o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
