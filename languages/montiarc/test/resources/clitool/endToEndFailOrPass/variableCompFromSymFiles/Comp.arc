/* (c) https://github.com/MontiCore/monticore */
package variableCompFromSymFiles;

component Comp {
  port in int pIn;
  Child c(true);
  pIn -> c.pIn;
  constraint(c.f1 || !c.f1);
}
