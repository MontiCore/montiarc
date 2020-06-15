/* (c) https://github.com/MontiCore/monticore */
package ifthenelse._ast;

import arcbasis._symboltable.PortSymbol;
import arcbasis.helper.ExpressionUtil;

import java.util.List;

public class ASTExecutionIfStatement extends ASTExecutionIfStatementTOP {
  public List<PortSymbol> getPortsInGuardExpression() {
    return ExpressionUtil.getPortsInGuardExpression(getGuard());
  }
}
