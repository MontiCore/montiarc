/* (c) https://github.com/MontiCore/monticore */
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
