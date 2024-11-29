/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModel {
  port in Double factor;
  port in String module;

  port out Double mbseCounter;
  port out Double saCounter;

  port in Double mbseCounted;
  port in Double saCounted;

  port out Double voteMBSE;
  port out Double voteSA;

  <<sync>> automaton{
    initial state mbse;
    state sa;
    state nonModule;

    mbse -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    mbse -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    sa -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    sa -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    nonModule -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    nonModule -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    mbse -> nonModule [module != "MBSE" && module != "SA"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    sa -> nonModule [module != "MBSE" && module != "SA"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };

    nonModule -> nonModule [module != "MBSE" && module != "SA"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
    };
  }
}
