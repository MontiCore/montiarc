/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ASTVariantComponentType extends ASTComponentType {

  protected ASTComponentType parent;

  protected Set<VariableArcVariationPoint> includedVariationPoints;

  protected VariantComponentTypeSymbol variantComponentTypeSymbol;

  public ASTVariantComponentType(ASTComponentType parent, VariantComponentTypeSymbol variantSymbol) {
    this.parent = parent;
    this.variantComponentTypeSymbol = variantSymbol;
    includedVariationPoints = variantSymbol.getIncludedVariationPoints();
    set_SourcePositionStart(parent.get_SourcePositionStart());
    set_SourcePositionEnd(parent.get_SourcePositionEnd());
  }

  @Override
  protected List<ASTArcElement> getArcElementList() {
    List<ASTArcElement> elementList = new ArrayList<>(super.getArcElementList());
    elementList.addAll(
      includedVariationPoints.stream().flatMap(vp -> vp.getElements().stream()).collect(Collectors.toList()));
    return elementList;
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
    return parent.getBody();
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
