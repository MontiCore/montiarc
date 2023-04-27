/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * Represents a configured component instance variant.
 */
public class VariantComponentInstanceSymbol extends ComponentInstanceSymbol {

  protected ComponentInstanceSymbol parent;

  public VariantComponentInstanceSymbol(@NotNull ComponentInstanceSymbol parent,
                                        @NotNull VariantCompTypeExpression type) {
    super(parent.getName());
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(type);

    this.setType(type);
    this.parent = parent;
  }
  @Override
  public IArcBasisScope getEnclosingScope() {
    return parent.getEnclosingScope();
  }

  @Override
  public ASTComponentInstance getAstNode() {
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
