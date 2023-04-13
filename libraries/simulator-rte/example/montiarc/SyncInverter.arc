/* (c) https://github.com/MontiCore/monticore */
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
