package montiarc._ast;

import java.util.List;

import com.google.common.collect.Lists;

public class ASTValueList extends ASTValueListTOP {
  
  public ASTValueList() {
    super();
  }

  public ASTValueList(java.util.List<ASTValuation> valuations, ASTValuation valuation) {
    super(valuations, valuation);
  }
  
  public List<ASTValuation> getAllValuations() {
    if (valuationIsPresent()) {
      return Lists.newArrayList(getValuation().get());
    } else {
      return getValuations();
    }
  }
}
