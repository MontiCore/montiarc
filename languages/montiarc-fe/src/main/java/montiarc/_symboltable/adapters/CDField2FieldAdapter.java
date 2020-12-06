/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ISymbolAdapter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDField2FieldAdapter extends FieldSymbol implements ISymbolAdapter<CDFieldSymbol> {

  protected final CDFieldSymbol adaptee;

  public CDField2FieldAdapter(@NotNull CDFieldSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.setType(CDSymTypeAdaptation.createSymType(adaptee.getType()));
  }

  @Override
  public CDFieldSymbol getAdaptee() {
    return this.adaptee;
  }

  @Override
  public SymTypeExpression getType() {
    return CDSymTypeAdaptation.createSymType(getAdaptee().getType());
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

  @Override
  public boolean isIsParameter() {
    return getAdaptee().isIsParameter();
  }
}