/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 * Port otherPort is not defined.
 */
component PortUsedInModeTransitionGuardDoesNotExist {

  port in String inS;
  port out String outS;

  modeautomaton {
    Mode1 -> Mode2 [inS.equals("Test")] / {outS = "this" + "is" + "a" + "test."};
    Mode2 -> Mode1 [otherPort == 2];

    mode Mode1 {}
    mode Mode2 {}

    initial Mode1;
  }
}