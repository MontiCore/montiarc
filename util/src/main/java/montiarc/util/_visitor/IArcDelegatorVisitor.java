/* (c) https://github.com/MontiCore/monticore */
package montiarc.util._visitor;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.mcbasics._visitor.MCBasicsVisitor;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsVisitor;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

import java.util.Optional;

public interface IArcDelegatorVisitor
  extends MCBasicsVisitor, MCLiteralsBasisVisitor, ExpressionsBasisVisitor {

  Optional<MCBasicsVisitor> getMCBasicsVisitor();

  void setMCBasicsVisitor(MCBasicsVisitor mCBasicsVisitor);

  Optional<MCLiteralsBasisVisitor> getMCLiteralsBasisVisitor();

  void setMCLiteralsBasisVisitor(MCLiteralsBasisVisitor mCLiteralsBasisVisitor);

  Optional<ExpressionsBasisVisitor> getExpressionsBasisVisitor();

  void setExpressionsBasisVisitor(ExpressionsBasisVisitor expressionsBasisVisitor);

  Optional<OOSymbolsVisitor> getOOSymbolsVisitor();

  void setOOSymbolsVisitor (OOSymbolsVisitor oOSymbolsVisitor);

  void setMCBasicTypesVisitor(MCBasicTypesVisitor mCBasicTypesVisitor);

  Optional<MCBasicTypesVisitor> getMCBasicTypesVisitor();

  void setBasicSymbolsVisitor(BasicSymbolsVisitor basicSymbolsVisitor);

  Optional<BasicSymbolsVisitor> getBasicSymbolsVisitor();
}