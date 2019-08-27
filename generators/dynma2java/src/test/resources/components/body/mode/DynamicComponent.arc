/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

component DynamicComponent {

    port in String stringIn;
    port out String stringOut;

    component Inner(String param) {
        port in String sIn;
        port out String sOut;

        compute {
          sOut = sIn + param;
        }
    }

    component Inner("1") embedded1;
    component Inner("2") embedded2;

  modeautomaton {
    mode Init {}

    mode Mode1 {
      use embedded1;
      connect stringIn -> embedded1.sIn;
      connect embedded1.sOut -> stringOut;
    }

    mode Mode2 {
      use embedded2;
      connect stringIn -> embedded2.sIn;
      connect embedded2.sOut -> stringOut;
    }

    initial Init;

    Init -> Mode1 [stringIn.equals("mode1")];
    Init -> Mode2 [stringIn.equals("mode2")];
    Mode1 -> Mode2 [stringIn.equals("mode2")];
    Mode2 -> Mode1 [stringIn.equals("mode1")];
    Mode1 -> Init [stringIn.equals("reset")];
    Mode2 -> Init [stringIn.equals("reset")];
  }
}