package components.body.automaton.transition.guards;

/**
 * Invalid model. Guards may not check values on outgoing ports.
 *
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly. (p. 103, Lst. 520)
 */
component GuardUsesOutgoingPort {

  int v;

  port 
    in int i,
    out int o;

  automaton AutomatonOutputInExpression{
     state S;
     initial S;

     S [i == v + i / o && v == (o+1)*o] / {o = (v*i)+o};
  }
}