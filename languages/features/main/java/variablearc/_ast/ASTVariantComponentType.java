/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import variablearc.VariableArcMill;
import variablearc._ast.util.ASTVariantBuilder;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represent a specific variant of a {@link ASTComponentType}
 */
public class ASTVariantComponentType extends ASTComponentType {

  protected ASTComponentType parent;

  protected Set<VariableArcVariationPoint> includedVariationPoints;

  protected VariableArcVariantComponentTypeSymbol variantComponentTypeSymbol;

  protected List<ASTArcElement> arcElementList;

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The variant (i.e. configuration) of this component
   */
  public ASTVariantComponentType(ASTComponentType parent, VariableArcVariantComponentTypeSymbol variantSymbol) {
    this.parent = parent;
    this.variantComponentTypeSymbol = variantSymbol;
    includedVariationPoints = variantSymbol.getIncludedVariationPoints();
    set_SourcePositionStart(parent.get_SourcePositionStart());
    set_SourcePositionEnd(parent.get_SourcePositionEnd());

    arcElementList = new ArrayList<>(parent.getBody().getArcElementList());
    arcElementList.addAll(
      includedVariationPoints.stream().flatMap(vp -> vp.getArcElements().stream()).collect(Collectors.toList()));
    ASTVariantBuilder builder = new ASTVariantBuilder(variantComponentTypeSymbol);
    arcElementList = arcElementList.stream().map(builder::duplicate).collect(Collectors.toList());
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
    return VariableArcMill.componentBodyBuilder().setArcElementsList(getArcElementList()).build();
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
