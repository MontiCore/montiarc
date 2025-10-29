/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.subcomponents;

import montiarc.types.OnOff;

component Forwarder {
  port
   in OnOff i,
   out OnOff o;

  automaton {
    initial state S;
    S -> S i / { o = i; }
  }
}
