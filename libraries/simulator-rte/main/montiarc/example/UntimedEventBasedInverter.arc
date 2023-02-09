/* (c) https://github.com/MontiCore/monticore */
package example;

/*
 * An inverter component.
 * Boolean and integer inputs are negated.
 *
 * This version is supposed to have time-unaware event-based inputs and time-unaware outputs. The stereotypes might not be entirely correct.
 */
component UntimedEventBasedInverter {
  port <<untimed>> in Boolean bIn;
  port <<untimed>> in Integer iIn;

  port out Boolean bOut;
  port out Integer iOut;

  automaton {
    initial state S;

    S -> S [bIn != null] / {
      bOut = !bIn;
    }

    S -> S [iIn != null] / {
      iOut = -1 * iIn;
    }
  }

}
