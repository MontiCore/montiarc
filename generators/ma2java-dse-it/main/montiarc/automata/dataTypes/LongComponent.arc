/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing long
 */
component LongComponent(Long parameter) {
  port <<sync>> in Long in;
  port <<sync>> out Long out;

  automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3]/{
      out = in;
    };

    Idle -> Idle /{
      out = parameter;
    };
  }
}
