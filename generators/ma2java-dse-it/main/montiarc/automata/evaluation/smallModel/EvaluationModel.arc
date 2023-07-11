/* (c) https://github.com/MontiCore/monticore */
package evaluation.smallModel;

/**
 * small model for the evaluation
 */
component EvaluationModel {
  port <<sync>> in Double factor;
  port <<sync>> in String module;

  port <<sync>> out Double mdseCounter;
  port <<sync>> out Double saCounter;

  port <<sync>> in Double mdseCounted;
  port <<sync>> in Double saCounted;

  port <<sync>> out Double voteMDSE;
  port <<sync>> out Double voteSA;

  automaton{
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
