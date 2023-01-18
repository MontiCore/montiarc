/* (c) https://github.com/MontiCore/monticore */
component ComplexImplies {
  feature a, b, c;

  constraint (!a || (b && c));
}
