/* (c) https://github.com/MontiCore/monticore */
package evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModel {
  port in Double factor;
  port in String module;

  port out Double mdseCounter;
  port out Double saCounter;

  port in Double mdseCounted;
  port in Double saCounted;

  port out Double voteMDSE;
  port out Double voteSA;

  <<sync>> automaton{
    initial state mdse;
    state sa;
    state nonModule;

    mdse -> mdse [module == "MDSE"]/{
      mdseCounter = factor;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    mdse -> sa [module == "SA"] /{
      mdseCounter = 0.0;
      saCounter = factor;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    sa -> sa [module == "SA"] /{
      mdseCounter = 0.0;
      saCounter = factor;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    sa -> mdse [module == "MDSE"]/{
      mdseCounter = factor;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    nonModule -> mdse [module == "MDSE"]/{
      mdseCounter = factor;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    nonModule -> sa [module == "SA"] /{
      mdseCounter = 0.0;
      saCounter = factor;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    mdse -> nonModule [module != "MDSE" && module != "SA"]/{
      mdseCounter = 0.0;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    sa -> nonModule [module != "MDSE" && module != "SA"]/{
      mdseCounter = 0.0;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };

    nonModule -> nonModule [module != "MDSE" && module != "SA"]/{
      mdseCounter = 0.0;
      saCounter = 0.0;
      voteMDSE = mdseCounted;
      voteSA = saCounted;
    };
  }
}
