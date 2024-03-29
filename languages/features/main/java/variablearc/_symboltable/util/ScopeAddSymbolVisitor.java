/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable.util;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._visitor.CompSymbolsVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

public class ScopeAddSymbolVisitor implements ArcBasisVisitor2, CompSymbolsVisitor2 {
  final protected IVariableArcScope scope;

  public ScopeAddSymbolVisitor(@NotNull IVariableArcScope scope) {
    Preconditions.checkNotNull(scope);
    this.scope = scope;
  }

  @Override
  public void visit(ArcPortSymbol node) {
    scope.add(node);
  }

  @Override
  public void visit(SubcomponentSymbol node) {
    scope.add(node);
  }
}
