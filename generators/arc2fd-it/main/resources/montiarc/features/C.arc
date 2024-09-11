/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component C {
  feature f1, f2, f3;

  constraint (f1 || f2);
  constraint (f2 || f3);
}
