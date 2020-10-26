/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ISymbolAdapter;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDType2TypeAdapter extends TypeSymbol implements ISymbolAdapter<CDTypeSymbol> {

  CDTypeSymbol adaptee;

  public CDType2TypeAdapter(@NotNull CDTypeSymbol adaptee) {
    super(adaptee.getName());
    
    this.adaptee = adaptee;
  }

  @Override
  public CDTypeSymbol getAdaptee() {
    return this.adaptee;
  }

  @Override
  public String getFullName() {
    return getAdaptee().getFullName();
  }

  @Override
  public void setFullName(String fullName) {
    getAdaptee().setFullName(fullName);
  }

  @Override
  public String getPackageName() {
    return getAdaptee().getPackageName();
  }

  @Override
  public void setPackageName(String packageName) {
    getAdaptee().setPackageName(packageName);
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getAdaptee().getAccessModifier();
  }
}