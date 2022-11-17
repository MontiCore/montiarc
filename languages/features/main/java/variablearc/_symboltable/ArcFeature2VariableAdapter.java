/* (c) https://github.com/MontiCore/monticore */

package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Adapts {@link ArcFeatureSymbol}s to {@link VariableSymbol}s, e.g., so that they can
 * easily be referred to from expressions.
 */
public class ArcFeature2VariableAdapter extends VariableSymbol {

  protected ArcFeatureSymbol adaptee;

  public ArcFeature2VariableAdapter(@NotNull ArcFeatureSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
    this.accessModifier = BasicAccessModifier.PUBLIC;
    this.type = SymTypeExpressionFactory.createPrimitive("boolean");
    this.isReadOnly = true;
  }

  protected ArcFeatureSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public void setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.getAdaptee().setName(name);
  }

  @Override
  public String getName() {
    return this.getAdaptee().getName();
  }

  @Override
  public String getFullName() {
    return this.getAdaptee().getFullName();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return this.getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return this.getAdaptee().getSourcePosition();
  }

  @Override
  public ArcFeature2VariableAdapter deepClone() {
    ArcFeature2VariableAdapter clone =
        new ArcFeature2VariableAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    clone.setIsReadOnly(this.isIsReadOnly());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if (this.getType() != null) {
      clone.setType(this.getType().deepClone());
    }
    return clone;
  }
}
