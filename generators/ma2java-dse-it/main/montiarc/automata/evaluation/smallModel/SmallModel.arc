/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.smallModel;

/**
 * small model for the evaluation
 */
component SmallModel(Integer parameter) {
  port sync in String module;
  port sync in Integer mtrNr;
  port sync out Double voteMBSE;
  port sync out Double voteSA;

  DistinctionModel distinction(parameter);
  EvaluationModel evaluation;
  Counter counterMBSE;
  Counter counterSA;


  mtrNr -> distinction.mtrNr;
  distinction.factor -> evaluation.factor;
  module -> evaluation.module;

  evaluation.mbseCounter -> counterMBSE.factor;
  evaluation.saCounter -> counterSA.factor;

  counterMBSE.out -> evaluation.mbseCounted;
  counterSA.out -> evaluation.saCounted;

  evaluation.voteMBSE -> voteMBSE;
  evaluation.voteSA -> voteSA;

}
