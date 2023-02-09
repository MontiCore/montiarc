/* (c) https://github.com/MontiCore/monticore */
package example;

/*
 * An inverter component.
 * Boolean and integer inputs are negated.
 *
 * This version is supposed to have time-aware synchronous inputs and time-aware outputs. The stereotypes might not be entirely correct.
 */
component SyncInverter {
  port <<sync>> in Boolean bIn;
  port <<sync>> in Integer iIn;

  port out Boolean bOut;
  port out Integer iOut;

  automaton {
    initial state S;

    S -> S / {
      bOut = !bIn;
      iOut = -1 * iIn;
    }
  }
}
