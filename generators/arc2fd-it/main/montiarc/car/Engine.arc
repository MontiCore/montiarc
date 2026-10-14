/* (c) https://github.com/MontiCore/monticore */
package car;

component Engine {
  feature electric, gas;

  constraint (electric || gas);
}
