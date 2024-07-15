/* (c) https://github.com/MontiCore/monticore */
package conformance.automaton2smt.reference;
import Datatypes.*;
import java.lang.String ;

component Reference {

  port in Input input;
  port out Output output;

  automaton {
    initial state LoggedIn;
    state NotLoggedIn;

    <<n="0">> NotLoggedIn -> LoggedIn [input == Input.LOGIN];

    <<n="1">> LoggedIn -> LoggedIn [input == Input.ACTION] / {
      output = Output.RESPONSE;
    };

    <<n="2">> LoggedIn -> NotLoggedIn [input == Input.LOGOUT];

    <<n="3">> NotLoggedIn -> NotLoggedIn [input == Input.ACTION] / {
      output = Output.ERROR;
    };

    <<n="4">> NotLoggedIn -> NotLoggedIn [input == Input.LOGOUT];
  }
}
