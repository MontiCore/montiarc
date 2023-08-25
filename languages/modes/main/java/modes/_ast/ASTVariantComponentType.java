/* (c) https://github.com/MontiCore/monticore */
package modes._ast;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import modes.ModesMill;
import modes._symboltable.ModesVariantComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a specific modes variant of a {@link ASTComponentType}
 */
public class ASTVariantComponentType extends ASTComponentType {

  protected ASTComponentType parent;
  protected ModesVariantComponentTypeSymbol variantComponentTypeSymbol;

  protected List<ASTArcElement> arcElementList;

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The variant (i.e. configuration) of this component
   */
  public ASTVariantComponentType(@NotNull ASTComponentType parent, @NotNull ModesVariantComponentTypeSymbol variantSymbol) {
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(variantSymbol);
    this.parent = parent;
    this.variantComponentTypeSymbol = variantSymbol;
    set_SourcePositionStart(parent.get_SourcePositionStart());
    set_SourcePositionEnd(parent.get_SourcePositionEnd());

    arcElementList = new ArrayList<>(parent.getBody().getArcElementList());
    arcElementList.addAll(variantSymbol.getMode().getAstNode().getBody().getArcElementList());
  }

  @Override
  protected List<ASTArcElement> getArcElementList() {
    return arcElementList;
  }

  @Override
  public String getName() {
    return parent.getName();
  }

  @Override
  public ASTComponentHead getHead() {
    return parent.getHead();
  }

  @Override
  public ASTComponentBody getBody() {
    return ModesMill.componentBodyBuilder().setArcElementsList(getArcElementList()).build();
  }

  @Override
  public boolean isPresentSymbol() {
    return variantComponentTypeSymbol != null;
  }

  @Override
  public ComponentTypeSymbol getSymbol() {
    return variantComponentTypeSymbol;
  }

  @Override
  public IArcBasisScope getSpannedScope() {
    return parent.getSpannedScope();
  }
}
