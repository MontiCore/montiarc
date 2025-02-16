/* (c) https://github.com/MontiCore/monticore */
package demo;

import Reference.*;

component Reference {

  port in Input input; // See "Reference.cd" for "Input" Datatype
  port out Output output; // See "Reference.cd" for "Output" Datatype

  automaton {
    initial state LoggedIn;
    state NotLoggedIn;

    NotLoggedIn -> LoggedIn [input == Input.LOGIN] input;

    LoggedIn -> LoggedIn [input == Input.ACTION] input / {
      output = Output.RESPONSE;
    };

    LoggedIn -> NotLoggedIn [input == Input.LOGOUT] input;

    NotLoggedIn -> NotLoggedIn [input == Input.ACTION] input / {
      output = Output.ERROR;
    };
  }
}
