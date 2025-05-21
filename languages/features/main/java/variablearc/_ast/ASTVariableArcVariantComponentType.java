/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcComponentType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.stream.Collectors;

/**
 * Represent a specific variant of a {@link ASTArcComponentType}
 */
public class ASTVariableArcVariantComponentType extends ASTVariantArcComponentType {

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The variant (i.e. configuration) of this component
   */
  public ASTVariableArcVariantComponentType(@NotNull ASTArcComponentType parent, @NotNull VariableArcVariantComponentTypeSymbol variantSymbol) {
    super(parent, variantSymbol, variantSymbol.getIncludedVariationPoints().stream().flatMap(vp -> vp.getArcElements().stream()).collect(Collectors.toList()));
  }
}
