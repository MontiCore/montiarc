/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class ComponentType2TypeSymbolAdapter extends TypeSymbol {

  protected ComponentTypeSymbol adaptee;

  public ComponentType2TypeSymbolAdapter(@NotNull ComponentTypeSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
    this.accessModifier = BasicAccessModifier.PUBLIC;
    this.spannedScope = adaptee.getSpannedScope();
  }

  protected ComponentTypeSymbol getAdaptee() {
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
  public IBasicSymbolsScope getSpannedScope() {
    return this.getAdaptee().getSpannedScope();
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
  public List<TypeVarSymbol> getTypeParameterList() {
    return super.getTypeParameterList();
  }
}
