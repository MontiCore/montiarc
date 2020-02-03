/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

component DynamicComponent {

    port in String stringIn;
    port out String stringOut;

    component Inner {
        port in String sIn;
        port out String sOut;
    }

    component Inner embedded1;
    component Inner embedded2;

  modeautomaton {
    mode Init {}

    mode Mode1 {
      use embedded1;
      connect stringIn -> embedded1.sIn;
      connect stringOut -> embedded1.sOut;
    }

    mode Mode2 {
      use embedded2;
      connect stringIn -> embedded2.sIn;
      connect stringOut -> embedded2.sOut;
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