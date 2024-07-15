/* (c) https://github.com/MontiCore/monticore */
package demo;

import Reference.*;

component Reference {

  port in Input input; // See "Reference.cd" for "Input" Datatype
  port out Output output; // See "Reference.cd" for "Output" Datatype

  automaton {
    initial state LoggedIn;
    state NotLoggedIn;

    NotLoggedIn -> LoggedIn [input == Input.LOGIN];

    LoggedIn -> LoggedIn [input == Input.ACTION] / {
      output = Output.RESPONSE;
    };

    LoggedIn -> NotLoggedIn [input == Input.LOGOUT];

    NotLoggedIn -> NotLoggedIn [input == Input.ACTION] / {
      output = Output.ERROR;
    };
  }
}