/* (c) https://github.com/MontiCore/monticore */
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
