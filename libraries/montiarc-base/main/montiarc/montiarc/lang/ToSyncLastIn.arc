/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

/** Converts a timed stream to a sync one by only forwarding the last message and discarding all others in one time slice */
component ToSyncLastIn<T>(T fallback) {

  port in T i;
  port sync out T o;

  T lastReceived = fallback;

  automaton {
    initial state S;

    S -> S i / { lastReceived = i; }
    S -> S / { o = lastReceived; lastReceived = fallback; }
  }
}
