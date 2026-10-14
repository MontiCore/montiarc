/* (c) https://github.com/MontiCore/monticore */

component ComplexSubcomponentFeatures {
  feature a, b;
  constraint ((!a || b) && s.c && s.d);

  component Sub s {
    feature c, d;
  }
}
