/* (c) https://github.com/MontiCore/monticore */
package evaluation.smallModel;

/**
 * small model for the evaluation
 */
component DistinctionModel(Integer parameter) {
  port <<sync>> in Integer mtrNr;
  port <<sync>> out Double factor;

  automaton{
    initial state Idle;

    Idle -> Idle [mtrNr < parameter] /{
      factor = 1.5;
    };

    Idle -> Idle [mtrNr > 350000] /{
      factor = 1.0;
    };
  }
}
