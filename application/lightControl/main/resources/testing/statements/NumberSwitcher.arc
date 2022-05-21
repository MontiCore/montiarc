/* (c) https://github.com/MontiCore/monticore */
package testing.statements;

/**
 * model for testing the generation of switch-statements,
 * which are with no equivalent in kotlin
 */
component NumberSwitcher {

  port in int number;
  port out String t;

  compute {
    int numberCopy = number;
    switch (numberCopy / 100) {
      case 1:
        t = "usualFirstCase";
        break;
      case 2:
      case 3:
        t = "doubleLabel";
        break;
      case 4:
        numberCopy *= 2;
      case 5:
        t = "noBreakBefore"+numberCopy;
        break;
      case 6:
        t = "usualMiddleCase";
        break;
      case 0:
        switch (number / 10) {
          case 1:
            t = "innerSwitch";
            break;
          case 2:
          case 3:
            t = "innerDouble";
            break;
          case 0:
          case 4:
            switch (number % 10){
              case 1:
                t = "innerInnerSwitch";
                numberCopy = 0;
                break;
              case 2:
                numberCopy += 400;
              case 4:
                t = "subSubFrom"+numberCopy;
                numberCopy = 0;
                break;
            }
            if (numberCopy == 0){
              break;
            }
          case 5:
            t = "defaultFromSubSubAnd50";
            break;
          default:
            t = "innerDefault";
        }
        break;
      case 7:
        t = "caseAfterInnerSwitch";
        break;
      case 8:
        if(number % 10 == 0){
          t = "ifInCase";
          break;
        }
      case 9:
        t = "elseFromBefore";
        break;
      case 10:
        if(number % 10 == 0){
          t = "otherIfInCase";
        } else if (number % 10 == 1){
          t = "elseIf";
        } else {
          t = "elseCase";
        }
        break;
      case 11:
      case 12:
        numberCopy++;
      case 13:
      default:
        t = "defaultFrom"+numberCopy;
    }
  }
}
