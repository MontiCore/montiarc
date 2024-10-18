/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component SendMessagesOnCondition {
  port in OnOff p;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;

    S -> S p / {
     if(p==OnOff.ON) o = p;
     if(p!=OnOff.ON) o = p;
   };
  }
}
