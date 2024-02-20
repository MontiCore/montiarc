/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component DistinctionModel(Integer parameter) {
  port in Integer mtrNr;
  port out Double factor;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [mtrNr < parameter] /{
      factor = 1.5;
    };

    Idle -> Idle [mtrNr > 350000] /{
      factor = 1.0;
    };
  }
}
