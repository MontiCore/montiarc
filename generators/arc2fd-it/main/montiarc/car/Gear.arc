/* (c) https://github.com/MontiCore/monticore */
package car;

component Gear {
  feature manual, automatic;

  constraint ((manual || automatic) && !(manual && automatic));
}
