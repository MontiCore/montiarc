/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component Engine {
  feature electric, gas;

  // Constraint for "Electric or Gas"
  constraint (electric || gas);
}
