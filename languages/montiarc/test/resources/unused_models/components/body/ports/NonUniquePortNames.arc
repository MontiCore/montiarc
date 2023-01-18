/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/**
 * Invalid model. The port names 'i', 'string', and 'object' are each
 * used twice.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace have to be unique. (p. 59. Lst. 3.31)
 * @implements [Wor16] MU1: The name of each component variable is unique among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
 */
component NonUniquePortNames {
  port
    in Integer i,
    in String,
    in Object object,
    out Integer i,
    out String,
    out Object;

  automaton {
    state Start;
    initial Start;

    Start -> Start;
  }
}
