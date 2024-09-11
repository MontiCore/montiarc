/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component Gear {
  feature manual, automatic;

  // Constraint for "manual XOR automatic"
  constraint ((manual || automatic) && !(manual && automatic));
}
