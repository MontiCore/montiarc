/* (c) https://github.com/MontiCore/monticore */
package automata.dataTypes;

/**
 *  Simple atomic component for testing long
 */
component LongComponent(Long parameter) {
  port in Long in;
  port out Long out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3]/{
      out = in;
    };

    Idle -> Idle /{
      out = parameter;
    };
  }
}
