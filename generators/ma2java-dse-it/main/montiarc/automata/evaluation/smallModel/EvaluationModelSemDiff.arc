/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModelSemDiff {
  port in Double factor;
  port in String module;

  port out Double mbseCounter;
  port out Double saCounter;

  port in Double mbseCounted;
  port in Double saCounted;

  port  out Double voteMBSE;
  port  out Double voteSA;

  port out Boolean chaosMBSE;
  port out Boolean chaosSA;

  <<sync>> automaton{
    initial state mbse;
    state sa;
    state nonModule;

    mbse -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    mbse -> mbse [module == "mbse&sa"]/{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    };

    mbse -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    sa -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    sa -> sa [module == "mbse&sa"] /{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    };

    sa -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    nonModule -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    nonModule -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    mbse -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    sa -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    nonModule -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    };

    nonModule -> nonModule [module == "mbse&sa"]/{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    };
  }
}