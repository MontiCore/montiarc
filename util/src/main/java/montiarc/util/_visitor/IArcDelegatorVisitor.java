/* (c) https://github.com/MontiCore/monticore */
package montiarc.util._visitor;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.mcbasics._visitor.MCBasicsVisitor;
import de.monticore.types.typesymbols._visitor.TypeSymbolsVisitor;

import java.util.Optional;

public interface IArcDelegatorVisitor
  extends MCBasicsVisitor, MCLiteralsBasisVisitor, ExpressionsBasisVisitor, TypeSymbolsVisitor {

  Optional<MCBasicsVisitor> getMCBasicsVisitor();

  void setMCBasicsVisitor(MCBasicsVisitor mCBasicsVisitor);

  Optional<MCLiteralsBasisVisitor> getMCLiteralsBasisVisitor();

  void setMCLiteralsBasisVisitor(MCLiteralsBasisVisitor mCLiteralsBasisVisitor);

  Optional<ExpressionsBasisVisitor> getExpressionsBasisVisitor();

  void setExpressionsBasisVisitor(ExpressionsBasisVisitor expressionsBasisVisitor);

  Optional<TypeSymbolsVisitor> getTypeSymbolsVisitor();

  void setTypeSymbolsVisitor(TypeSymbolsVisitor typeSymbolsVisitor);

}