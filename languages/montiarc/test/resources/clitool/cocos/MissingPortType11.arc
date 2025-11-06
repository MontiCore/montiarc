/* (c) https://github.com/MontiCore/monticore */
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
