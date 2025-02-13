/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModel {
  port sync in Double factor;
  port sync in String module;

  port sync out Double mbseCounter;
  port sync out Double saCounter;

  port sync in Double mbseCounted;
  port sync in Double saCounted;

  port sync out Double voteMBSE;
  port sync out Double voteSA;

  automaton{
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
