/* (c) https://github.com/MontiCore/monticore */
package prepostcondition._ast;

import arcbasis._symboltable.PortSymbol;
import arcbasis.helper.ExpressionUtil;

import java.util.List;

public class ASTPrecondition extends ASTPreconditionTOP {
  public List<PortSymbol> getPortsInGuardExpression() {
    return ExpressionUtil.getPortsInGuardExpression(getGuard());
  }

  @Override public String toString() {
    return ExpressionUtil.printExpression(this.getGuard());
  }
}
