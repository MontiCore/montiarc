/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import montiarc.types.OnOff;

component OptionalDelay {
  feature delayed;

  port in OnOff i;
  varif (delayed) {
    port <<delayed>> out OnOff o;
    <<timed>> automaton {
        initial { o = OnOff.OFF; } state S;

        S -> S i / {
          o = i;
        };
      }
  } else {
    port out OnOff o;
    <<timed>> automaton {
        initial state S;

        S -> S i / {
          o = i;
        };
      }
  }
}
