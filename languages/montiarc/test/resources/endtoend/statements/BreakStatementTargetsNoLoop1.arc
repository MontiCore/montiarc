/* (c) https://github.com/MontiCore/monticore */
package statements;

component BreakStatementTargetsNoLoop1 {
  port sync in int i;
  compute {
    switch (i) {
      case 1: {
        break;
      }
      default: { }
    }
  }
}
