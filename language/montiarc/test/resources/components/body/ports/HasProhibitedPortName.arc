/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model.
 * Uses prohibited port names
 */
 component HasProhibitedPortName{
  port in String r__input,
        in String r__currentState,
        in String r__result,
        in String r__behaviorImpl;

  automaton Implementation{
    state Initial;
    initial Initial;
  }
}
