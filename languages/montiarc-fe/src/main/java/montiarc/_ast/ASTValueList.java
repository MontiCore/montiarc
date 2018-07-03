package montiarc._ast;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

public class ASTValueList extends ASTValueListTOP {
  
  /**
   * Constructor for montiarc._ast.ASTValueList
   */
  protected ASTValueList() {
    super();
  }
  
  /**
   * Constructor for montiarc._ast.ASTValueList
   * 
   * @param valuations
   * @param valuation
   */
  public ASTValueList(java.util.List<ASTValuation> valuations, Optional<ASTValuation> valuation, Optional<ASTNoData> nodata) {
    super(valuations, valuation, nodata);
  }
  
  public List<ASTValuation> getAllValuations() {
    if (isPresentValuation()) {
      return Lists.newArrayList(getValuation());
    }
    else {
      return getValuationList();
    }
  }
  
}
