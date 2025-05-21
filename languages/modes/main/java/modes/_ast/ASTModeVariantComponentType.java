/* (c) https://github.com/MontiCore/monticore */
package modes._ast;

import arcbasis._ast.ASTArcComponentType;
import modes._symboltable.ModesVariantComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariantArcComponentType;

/**
 * Represent a specific modes variant of a {@link ASTArcComponentType}
 */
public class ASTModeVariantComponentType extends ASTVariantArcComponentType {

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The variant (i.e. configuration) of this component
   */
  public ASTModeVariantComponentType(@NotNull ASTArcComponentType parent, @NotNull ModesVariantComponentTypeSymbol variantSymbol) {
    super(parent, variantSymbol, variantSymbol.getMode().getAstNode().getBody().getArcElementList());
  }
}
