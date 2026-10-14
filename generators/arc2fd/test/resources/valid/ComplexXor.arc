/* (c) https://github.com/MontiCore/monticore */

component ComplexXor {
  feature a, b;
  constraint ((a && !b) || (!a && b));
}
