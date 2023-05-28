/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing strings
 */
component StringComponent {
  port <<sync>> in String in;
  port <<sync>> out String out;

  automaton{
    initial state Idle;

    Idle -> Idle [in == "helloWorld"]/{
      out = "moin";
    };

    Idle -> Idle /{
      out = "helloWorld";
    };
  }
}
