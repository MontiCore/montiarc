/* (c) https://github.com/MontiCore/monticore */
package modes._ast;

import arcbasis._ast.ASTComponentType;
import modes._symboltable.ModesVariantComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariantComponentType;

/**
 * Represent a specific modes variant of a {@link ASTComponentType}
 */
public class ASTModeVariantComponentType extends ASTVariantComponentType {

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The variant (i.e. configuration) of this component
   */
  public ASTModeVariantComponentType(@NotNull ASTComponentType parent, @NotNull ModesVariantComponentTypeSymbol variantSymbol) {
    super(parent, variantSymbol, variantSymbol.getMode().getAstNode().getBody().getArcElementList());
  }
}
