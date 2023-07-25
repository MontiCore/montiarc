/* (c) https://github.com/MontiCore/monticore */
package automata;

component EventInverter {
  port <<timed>> in Boolean bIn;
  port <<timed>> in Integer iIn;

  port out Boolean bOut;
  port out Integer iOut;

  automaton {
    initial state S;

    S -> S [bIn != null] bIn / {
      java.lang.System.out.println("Message stimulus bIn.");
      bOut = !bIn;
    }

    S -> S [iIn != null] iIn / {
      java.lang.System.out.println("Message stimulus iIn.");
      iOut = -1 * iIn;
    }

    S -> S Tick / {
      java.lang.System.out.println("Tick stimulus.");
    }

    S -> S / {
      java.lang.System.out.println("Stimulus-free transition.");
    }
  }
}
