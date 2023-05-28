/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 * Simple atomic component for testing characters
 */
component CharactersComponent {
  port <<sync>> in Character in;
  port <<sync>> out Character out;

  automaton{
    initial state Idle;

    Idle -> Idle [in == 'c'] /{
      out = 'd';
    };

    Idle -> Idle /{
      out = 'z';
    };
  }
}
