package de.monticore.automaton.ioautomaton._ast;

import java.util.List;

import com.google.common.collect.Lists;

public class ASTValueList extends ASTValueListTOP {
  
  public ASTValueList() {
    super();
  }

  public ASTValueList(java.util.List<ASTValuationExt> valuations, ASTValuationExt valuation) {
    super(valuations, valuation);
  }
  
  public List<ASTValuationExt> getAllValuations() {
    if (valuationIsPresent()) {
      return Lists.newArrayList(getValuation().get());
    } else {
      return getValuations();
    }
  }
}
