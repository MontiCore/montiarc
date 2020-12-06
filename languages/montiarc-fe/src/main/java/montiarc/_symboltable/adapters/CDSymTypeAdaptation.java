/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDSymTypeAdaptation {

  public static SymTypeExpression createSymType(@NotNull CDTypeSymbolLoader cdLoader) {
    return SymTypeExpressionFactory.createTypeObject(cdLoader.getName(),
      cdLoader.getEnclosingScope());
  }

  public static SymTypeExpression createSymType(@NotNull CDTypeSymbol cdType) {
    return SymTypeExpressionFactory.createTypeObject(cdType.getName(), cdType.getEnclosingScope());
  }

  public static MethodSymbol createMethod(@NotNull CDMethOrConstrSymbol methodToAdapt) {
    return TypeSymbolsMill.methodSymbolBuilder()
      .setName(methodToAdapt.getName())
      .setReturnType(createSymType(methodToAdapt.getReturnType()))
      .setAccessModifier(methodToAdapt.getAccessModifier())
      .setIsStatic(methodToAdapt.isIsStatic())
      .setSpannedScope(methodToAdapt.getSpannedScope())
      .setEnclosingScope(methodToAdapt.getEnclosingScope())
      .build();
  }

  public static FieldSymbol createField(@NotNull CDFieldSymbol fieldToAdapt) {
    return TypeSymbolsMill.fieldSymbolBuilder()
      .setName(fieldToAdapt.getName())
      .setAccessModifier(fieldToAdapt.getAccessModifier())
      .setIsParameter(fieldToAdapt.isIsParameter())
      .setType(createSymType(fieldToAdapt.getType()))
      .build();
  }
}