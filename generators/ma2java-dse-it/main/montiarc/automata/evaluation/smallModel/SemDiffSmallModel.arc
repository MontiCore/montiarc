/* (c) https://github.com/MontiCore/monticore */
package evaluation.smallModel;

/**
 * small model for the evaluation
 */
component SemDiffSmallModel(Integer parameter) {
  port <<sync>> in Integer mtrNr;
  port <<sync>> in String module;
  port <<sync>> out Double voteMDSE;
  port <<sync>> out Double voteSA;

  DistinctionModel distinction(parameter);
  EvaluationModel evaluation;
  Counter counterMDSE;
  Counter counterSA;


  mtrNr -> distinction.mtrNr;
  distinction.factor -> evaluation.factor;
  module -> evaluation.module;

  evaluation.mdseCounter -> counterMDSE.factor;
  evaluation.saCounter -> counterSA.factor;

  counterMDSE.out -> evaluation.mdseCounted;
  counterSA.out -> evaluation.saCounted;

  evaluation.voteMDSE -> voteMDSE;
  evaluation.voteSA -> voteSA;

}
