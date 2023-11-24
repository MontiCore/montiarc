/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 * Simple atomic component for testing characters
 */
component CharactersComponent {
  port in Character in;
  port out Character out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in == 'c'] /{
      out = 'd';
    };

    Idle -> Idle /{
      out = 'z';
    };
  }
}
