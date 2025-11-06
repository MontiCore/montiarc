/* (c) https://github.com/MontiCore/monticore */
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
