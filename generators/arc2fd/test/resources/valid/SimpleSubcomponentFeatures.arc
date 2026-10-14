/* (c) https://github.com/MontiCore/monticore */

component SimpleSubcomponentFeatures {
  feature a;
  constraint (a && s.b);

  component Sub s {
    feature b;
  }
}
