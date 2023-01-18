/* (c) https://github.com/MontiCore/monticore */
component SimpleExcludes {
  feature a, b;

  constraint (!(a && b));
}
