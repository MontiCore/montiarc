/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModelSemDiff {
  port sync in Double factor;
  port sync in String module;

  port sync out Double mbseCounter;
  port sync out Double saCounter;

  port sync in Double mbseCounted;
  port sync in Double saCounted;

  port sync out Double voteMBSE;
  port sync out Double voteSA;

  port sync out Boolean chaosMBSE;
  port sync out Boolean chaosSA;

  automaton{
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
    }

    mbse -> mbse [module == "mbse&sa"]/{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    }

    mbse -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    sa -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    sa -> sa [module == "mbse&sa"] /{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    }

    sa -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    nonModule -> mbse [module == "MBSE"]/{
      mbseCounter = factor;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    nonModule -> sa [module == "SA"] /{
      mbseCounter = 0.0;
      saCounter = factor;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    mbse -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    sa -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    nonModule -> nonModule [module != "MBSE" && module != "SA" && module != "mbse&sa"]/{
      mbseCounter = 0.0;
      saCounter = 0.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = false;
      chaosSA = false;
    }

    nonModule -> nonModule [module == "mbse&sa"]/{
      mbseCounter = 2.0;
      saCounter = 2.0;
      voteMBSE = mbseCounted;
      voteSA = saCounted;
      chaosMBSE = true;
      chaosSA = true;
    }
  }
}
