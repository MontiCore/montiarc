/* (c) https://github.com/MontiCore/monticore */
component MissingPortType4 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out Missing o;
  }
}
