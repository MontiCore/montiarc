/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDSymTypeAdaptation {

  public static SymTypeExpression createSymType(@NotNull CDTypeSymbol cdType) {
    return SymTypeExpressionFactory.createTypeObject(cdType.getName(), cdType.getEnclosingScope());
  }

  public static MethodSymbol createMethod(@NotNull MethodSymbol methodToAdapt) {
    return MontiArcMill.methodSymbolBuilder()
      .setName(methodToAdapt.getName())
      .setReturnType(methodToAdapt.getReturnType())
      .setAccessModifier(methodToAdapt.getAccessModifier())
      .setIsStatic(methodToAdapt.isIsStatic())
      .setSpannedScope(methodToAdapt.getSpannedScope())
      .setEnclosingScope(methodToAdapt.getEnclosingScope())
      .build();
  }

  public static FieldSymbol createField(@NotNull FieldSymbol fieldToAdapt) {
    return MontiArcMill.fieldSymbolBuilder()
      .setName(fieldToAdapt.getName())
      .setAccessModifier(fieldToAdapt.getAccessModifier())
      .setType(fieldToAdapt.getType())
      .build();
  }
}