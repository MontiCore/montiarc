/* (c) https://github.com/MontiCore/monticore */

component Derived extends Base {
  feature y;

  constraint (!y || x);
}
