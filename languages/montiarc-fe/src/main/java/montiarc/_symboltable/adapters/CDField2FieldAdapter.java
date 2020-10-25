/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ISymbolAdapter;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDField2FieldAdapter extends FieldSymbol implements ISymbolAdapter<FieldSymbol> {

  protected final FieldSymbol adaptee;

  public CDField2FieldAdapter(@NotNull FieldSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.setType(adaptee.getType());
  }

  @Override
  public FieldSymbol getAdaptee() {
    return this.adaptee;
  }

  @Override
  public SymTypeExpression getType() {
    return getAdaptee().getType();
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public String getPackageName() {
    return getAdaptee().getPackageName();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getAdaptee().getAccessModifier();
  }

  @Override
  public boolean isIsStatic() {
    return getAdaptee().isIsStatic();
  }
}