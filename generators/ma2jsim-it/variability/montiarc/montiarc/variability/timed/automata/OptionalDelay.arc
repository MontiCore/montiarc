/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.timed.automata;

import montiarc.types.OnOff;

component OptionalDelay {
  feature delayed;

  port in OnOff i;
  varif (delayed) {
    port out OnOff o;
    <<delayed>> automaton {
        initial state S;

        S -> S i / {
          o = i;
        }
      }
  } else {
    port out OnOff o;
    automaton {
        initial state S;

        S -> S i / {
          o = i;
        }
      }
  }
}
