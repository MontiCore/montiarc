/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing strings
 */
component StringComponent {
  port in String in;
  port out String out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in == "helloWorld"]/{
      out = "moin";
    };

    Idle -> Idle /{
      out = "helloWorld";
    };
  }
}
