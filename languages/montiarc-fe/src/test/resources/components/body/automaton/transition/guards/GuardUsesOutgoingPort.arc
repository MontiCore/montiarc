/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.guards;

/**
 * Invalid model.
 * Guards may not check values on outgoing ports.
 *
 * @implements [RRW14a] T6: The direction of ports has to be respected.
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used
 * correctly. (p. 103, Lst. 520)
 */
component GuardUsesOutgoingPort {

  int v;

  port 
    in int i,
    out String s,
    out int o;

  automaton AutomatonOutputInExpression{
     state S;
     initial S;

     S [i == v + i / o && v == (o+1)*o && s.equals("Hello World") && o>=6] / {call s.equals("Hello World"), i=6, o = (v*i)+o};
     //______________^__________^____^____^__________________________^_____________^________________________^______________^
  }
}
