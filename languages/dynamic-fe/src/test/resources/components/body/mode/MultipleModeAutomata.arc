/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 * Multiple mode controllers present.
 */
component MultipleModeAutomata {

  component Adder add;

  modeautomaton Aut1 {
      mode Mode1 {}
      mode Mode2 {}

      initial Mode1;

      Mode1 -> Mode2;
  }

  modeautomaton Aut2 {
      mode Mode1 {}
      mode Mode2 {}

      initial Mode1;

      Mode1 -> Mode2;
  }

}