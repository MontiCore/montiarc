/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponent;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Represents a configured component instance variant.
 */
public class VariantSubcomponentSymbol extends SubcomponentSymbol {

  protected SubcomponentSymbol parent;

  public VariantSubcomponentSymbol(@NotNull SubcomponentSymbol parent,
                                        @NotNull CompKindExpression type) {
    super(parent.getName());
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(type);

    this.setType(type);
    this.parent = parent;
  }

  public SubcomponentSymbol getOriginal() {
    return parent;
  }

  @Override
  public ICompSymbolsScope getEnclosingScope() {
    return parent.getEnclosingScope();
  }

  @Override
  public ASTSubcomponent getAstNode() {
    return parent.getAstNode();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return parent.getSourcePosition();
  }

  @Override
  public boolean isPresentAstNode() {
    return parent.isPresentAstNode();
  }

  @Override
  public String getPackageName() {
    return parent.getPackageName();
  }
}
