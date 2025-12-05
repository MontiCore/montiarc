/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg events 'msg1', 'msg2', and 'msg3' are missing
 * (the event symbols cannot be resolved).
 */
component MissingEvent6 {

  automaton {
    initial state S1 {
      S1 -> S1 msg1;
    }

    state S2 {
      S2 -> S1 msg2;
      state S3 {
        S3 -> S1 msg3;
      }
    }
  }
}
