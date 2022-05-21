/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

/**
 * builds up on "DoActions"
 * tests whether exit-, transition- and then entry- actions are executed - in that order
 */
component OrderOfActions {

  port out String text;

  String localString = "start";

  automaton {
    initial state Start {
      exit / {
        localString += "-exit";
      }
    };
    state End {
      entry / {
        localString += "-entry";
      }
    };

    Start -> End / {
      localString += "-reaction";
      text = "took transition";
    };

    End -> End / {
      text = localString;
    };
  }
}