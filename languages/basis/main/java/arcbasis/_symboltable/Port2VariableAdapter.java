/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Adapts {@link ArcPortSymbol}s to {@link VariableSymbol}s, e.g., so that they can
 * easily be referred to from expressions.
 */
public class Port2VariableAdapter extends VariableSymbol {

  protected ArcPortSymbol adaptee;

  public Port2VariableAdapter(@NotNull ArcPortSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
    this.accessModifier = BasicAccessModifier.PUBLIC;
  }

  public ArcPortSymbol getAdaptee() {
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
  public void setType(@NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.getAdaptee().setType(type);
  }

  @Override
  public SymTypeExpression getType() {
    return this.getAdaptee().getType();
  }

  @Override
  public boolean isIsReadOnly() {
    return this.getAdaptee().isIncoming();
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
  public Port2VariableAdapter deepClone() {
    Port2VariableAdapter clone = new Port2VariableAdapter(this.getAdaptee());
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
