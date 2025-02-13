/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component SemDiffSmallModel(Integer parameter) {
  port sync in Integer mtrNr;
  port sync in String module;
  port sync out Double voteMBSE;
  port sync out Double voteSA;

  DistinctionModel distinction(parameter);
  EvaluationModelSemDiff evaluation;
  CounterSemDiff counterMBSE;
  CounterSemDiff counterSA;


  mtrNr -> distinction.mtrNr;
  distinction.factor -> evaluation.factor;
  module -> evaluation.module;

  evaluation.mbseCounter -> counterMBSE.factor;
  evaluation.saCounter -> counterSA.factor;

  evaluation.chaosMBSE -> counterMBSE.chaos;
  evaluation.chaosSA -> counterSA.chaos;

  counterMBSE.out -> evaluation.mbseCounted;
  counterSA.out -> evaluation.saCounted;

  evaluation.voteMBSE -> voteMBSE;
  evaluation.voteSA -> voteSA;

}
