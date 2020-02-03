/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component OutgoingPortInModeTransition(Integer param) {

  port in String inS;
  port out String outS;

  String variable;

  component Adder add;

  modeautomaton {
    mode Mode1, Mode2 {
      use add;
    }

    mode Mode2 {}

    Mode1 -> Mode2 [outS.equals("A") && param == 5]; // 2 Errors
    Mode1 -> Mode2 [outS.equals("B")] / {inS = "Written to Input"}; // 2 Errors
    Mode1 -> Mode2 / {outS = "Err: Written to output in mode trans."}; // 1 Error
    Mode1 -> Mode2 / {call String.valueOf(50)}; // 1 Error
    Mode2 -> Mode1 [variable.equals("Test")] / {variable = outS}; // 1 Error
    Mode2 -> Mode1 [!variable.equals("Test")] / {variable = "1" + param}; // 1 Error

    initial Mode1;
  }
}