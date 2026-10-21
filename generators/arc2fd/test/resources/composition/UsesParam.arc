/* (c) https://github.com/MontiCore/monticore */

component UsesParam {
  feature g;

  // binds n = 1, hence p.f is mandatory
  Param p(1);

  constraint (p.f);
}
