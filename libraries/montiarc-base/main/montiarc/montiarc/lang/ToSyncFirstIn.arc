/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

/** Converts a timed stream to a sync one by only forwarding the first message and discarding all others in one time slice */
component ToSyncFirstIn<T>(T fallback) {

  port <<timed>> in T i;
  port <<sync>> out T o;

  <<timed>> automaton {
    initial state Waiting;
    state Sent;

    Waiting -> Sent i / { o = i; };
    Sent -> Sent i / {}; // discard
    Sent -> Waiting / {}; // reset
    Waiting -> Waiting / { o = fallback; };
  }
}
