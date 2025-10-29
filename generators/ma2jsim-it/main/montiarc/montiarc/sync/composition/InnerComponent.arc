/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;

component InnerComponent {

  port sync in OnOff i;
  port sync out OnOff o;

  i -> sub.i;
  sub.o -> o;

  component InnerMedium sub {
    port sync in OnOff i;
    port sync out OnOff o;

    i -> sub.i;
    sub.o -> o;

    component Medium sub {
      port sync in OnOff i;
      port sync out OnOff o;

      automaton {
        initial state S;

        S -> S / {
          o = i;
        }
      }
    }
  }
}
