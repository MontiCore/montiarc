/* (c) https://github.com/MontiCore/monticore */
package statements;

component BreakStatementTargetsNoLoop2 {
  port sync in int i;
  compute {
    switch (i) {
      case 1: {
        if (i == 1) {
          break;
        }
      }
      default: { }
    }
  }
}
