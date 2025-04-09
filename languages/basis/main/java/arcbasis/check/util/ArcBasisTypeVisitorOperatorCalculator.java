/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypeExpressionFactory.createStringType;

public class ArcBasisTypeVisitorOperatorCalculator extends TypeVisitorOperatorCalculator {

  public static void init() {
    Log.trace("init ArcBasisTypeVisitorOperatorCalculator", "TypeCheck setup");
    setDelegate(new ArcBasisTypeVisitorOperatorCalculator());
  }

  protected SymTypeExpression calculateToString(SymTypeExpression type) {
    SymTypeExpression strType;
    if (type.isRegExType()) {
      strType = type.deepClone();
    } else {
      strType = createStringType();
    }
    return strType;
  }
}
