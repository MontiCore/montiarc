/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component AG {

  port in OnOff i;
  port out OnOff o;

  // This is just a placeholder that it does not impact generation
  assume: i.len() > 0;
  guarantee: o.len() != 0;
}
