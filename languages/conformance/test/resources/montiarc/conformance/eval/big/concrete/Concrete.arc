/* (c) https://github.com/MontiCore/monticore */
package conformance.eval.big.concrete;
import Datatypes.*;
import java.lang.String ;
/*
 * Parsable Model.
 */
component Concrete {

  port in Input input;
  port out Output output;

  automaton {
    initial state Start;
            state Connecting ;
            state Registering;
            state OnDashboard;
            state OnProfile;
            state OnSearchResult;
            state Searching;
            state InCart;
            state Confirming;
            state CheckingOut;
            state OnProduct;

    Start -> Connecting [input == Input.CONNECT_BTN] / {
      output = Output.ACTION_DONE;
    };

    Start -> Registering [input == Input.REGISTER_BTN] / {
      output = Output.ACTION_DONE;
    };

    Registering -> Connecting [input == Input.REGISTER_BTN] / {
      output = Output.ACTION_DONE;
    };

    Connecting -> OnDashboard [input == Input.LOG_IN_BTN];

    OnDashboard -> OnProfile [input == Input.VIEW_PROFILE_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnDashboard -> Searching [input == Input.SEARCH_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnDashboard -> InCart [input == Input.CART_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnDashboard -> Start [input == Input.LOG_OUT_BTN];

    OnProfile -> OnDashboard [input == Input.DASHBOARD_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnProfile -> OnProfile [input == Input.EDIT_PROFILE_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnProfile -> Start [input == Input.LOG_OUT_BTN];

    Searching -> Searching [input == Input.ENTER_TEXT_BTN] / {
      output = Output.ACTION_DONE;
    };
    Searching -> OnSearchResult [input == Input.SEARCH_BTN] / {
      output = Output.ACTION_DONE;
    };

    Searching -> Start [input == Input.LOG_OUT_BTN];

    OnSearchResult -> OnProduct [input == Input.CHOOSE_PRODUCT_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnSearchResult -> Start [input == Input.LOG_OUT_BTN];

    OnProduct -> InCart [input == Input.ADD_TO_CART_BTN];//not conforming

    OnProduct -> OnDashboard [input == Input.DASHBOARD_BTN] / {
      output = Output.ACTION_DONE;
    };

    OnProduct -> Start [input == Input.LOG_OUT_BTN] ;

    InCart -> CheckingOut [input == Input.CHECKOUT_BTN] / {
      output = Output.ACTION_DONE;
    };

    InCart -> OnDashboard [input == Input.CONTINUE_BTN] / {
      output = Output.ACTION_DONE;
    };

    InCart -> Start [input == Input.LOG_OUT_BTN];

    CheckingOut -> Confirming [input == Input.CONFIRMATION_BTN] / {
      output = Output.ACTION_DONE;
    };

    CheckingOut -> Start [input == Input.LOG_OUT_BTN];

    Confirming -> OnDashboard [input == Input.CONTINUE_BTN] / {
      output = Output.ACTION_DONE;
    };

    Confirming -> Start [input == Input.LOG_OUT_BTN];
  }
}
