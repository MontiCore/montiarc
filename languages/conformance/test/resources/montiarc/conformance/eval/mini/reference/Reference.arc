/* (c) https://github.com/MontiCore/monticore */
package conformance.eval.mini.reference;
import Datatypes.*;
import java.lang.String;

component Reference {

port in Input input;
port out Output output;

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
