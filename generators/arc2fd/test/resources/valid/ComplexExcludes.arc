/* (c) https://github.com/MontiCore/monticore */

component ComplexExcludes {
  feature a, b, c;

  constraint (!a || !b || !c);
}
