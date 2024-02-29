/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;

component InnerComponent {

  port in OnOff i;
  port out OnOff o;

  i -> sub.i;
  sub.o -> o;

  component InnerMedium sub {
    port in OnOff i;
    port out OnOff o;

    i -> sub.i;
    sub.o -> o;

    component Medium sub {
      port in OnOff i;
      port out OnOff o;

      <<sync>> automaton {
        initial state S;

        S -> S / {
          o = i;
        };
      }
    }
  }
}
