/* (c) https://github.com/MontiCore/monticore */

component SimpleOptional {
  feature a;

  constraint (a || !a);
}
