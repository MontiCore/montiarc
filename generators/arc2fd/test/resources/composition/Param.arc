/* (c) https://github.com/MontiCore/monticore */

component Param(int n) {
  feature f;

  // f is mandatory, iff n <= 3
  constraint (n > 3 || f);
}
