package elevator;

component Door {
  port in boolean shouldOpen;

  automaton {
    initial state Closed;
    state Open;

    Closed -> Open [shouldOpen == true] shouldOpen / {
      System.out.println("Door has opened");
    }
    Open -> Closed [shouldOpen == false] shouldOpen / {
      System.out.println("Door has closed");
    }
  }
}
