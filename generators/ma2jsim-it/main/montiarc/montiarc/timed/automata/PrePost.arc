/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component PrePost {

  port in OnOff i;
  port out OnOff o;

  // These conditions need to be checked for correctness again.
  //  This is just a placeholder that it does not impact generation
  pre: i == o;
  post: i == o;
}
