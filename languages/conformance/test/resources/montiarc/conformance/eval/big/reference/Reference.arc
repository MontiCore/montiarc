/* (c) https://github.com/MontiCore/monticore */
package conformance.eval.big.reference;
import Datatypes.*;
import java.lang.String ;

component Reference {

  port in Input input;
  port out Output output;

  automaton {

    initial state LoggedIn;
            state LoggedOut;

    LoggedOut -> LoggedIn [input == Input.LOGIN] ;
    LoggedIn -> LoggedOut [input == Input.LOGOUT] ;

    LoggedIn -> LoggedIn [input == Input.POST_LOGIN_ACTION] / {
      output = Output.RESPONSE;
    };

    LoggedIn -> LoggedIn [input == Input.PRE_LOGIN_ACTION] / {
      output = Output.ERROR;
    };


    LoggedOut -> LoggedOut [input == Input.PRE_LOGIN_ACTION] / {
      output = Output.RESPONSE ;
    };

    LoggedOut -> LoggedOut [input == Input.POST_LOGIN_ACTION] / {
      output = Output.ERROR ;
    };


 }
}
