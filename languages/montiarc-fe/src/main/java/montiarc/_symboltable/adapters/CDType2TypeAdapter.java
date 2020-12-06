/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ISymbolAdapter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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

  @Override
  public boolean isClass() {
    return getAdaptee().isIsClass();
  }

  @Override
  public boolean isInterface() {
    return getAdaptee().isIsInterface();
  }

  @Override
  public boolean isEnum() {
    return getAdaptee().isIsEnum();
  }

  @Override
  public boolean isAbstract() {
    return getAdaptee().isIsAbstract();
  }

  @Override
  public List<FieldSymbol> getFieldList() {
    return getAdaptee().getFields().stream()
      .map(CDSymTypeAdaptation::createField)
      .collect(Collectors.toList());
  }

  @Override
  public List<FieldSymbol> getFieldList(String fieldname) {
    return getAdaptee().getFields().stream()
      .filter(fieldSymbol -> fieldSymbol.getName().equals(fieldname))
      .map(CDSymTypeAdaptation::createField)
      .collect(Collectors.toList());
  }

  @Override
  public List<MethodSymbol> getMethodList() {
    return getAdaptee().getMethods().stream()
      .map(CDSymTypeAdaptation::createMethod)
      .collect(Collectors.toList());
  }

  @Override
  public List<MethodSymbol> getMethodList(String methodName) {
    return getAdaptee().getMethods().stream()
      .filter(methodSymbol -> methodSymbol.getName().equals(methodName))
      .map(CDSymTypeAdaptation::createMethod)
      .collect(Collectors.toList());
  }

  @Override
  public List<SymTypeExpression> getSuperClassesOnly() {
    return getAdaptee().getSuperTypesTransitive().stream()
      .map(CDSymTypeAdaptation::createSymType)
      .collect(Collectors.toList());
  }
}