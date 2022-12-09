/* (c) https://github.com/MontiCore/monticore */

component ComplexExcludes {
  feature a, b;

  constraint (!a || !b || !c);
}
