/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.util.ASTVariantBuilder;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An abstract AST component variant implementation.
 * Can be used as a starting point for implementing custom variants (e.g. {@link ASTVariableArcVariantComponentType}).
 */
public abstract class ASTVariantComponentType extends ASTComponentType {

  protected ASTComponentType parent;

  /**
   * @param parent                   The component this variant originates from
   * @param variantSymbol            The variant (i.e. configuration) of this component
   * @param additionalArcElementList Additional ASTArcElements added by this variant to the top level
   */
  public ASTVariantComponentType(@NotNull ASTComponentType parent, @NotNull VariantComponentTypeSymbol variantSymbol, @NotNull List<ASTArcElement> additionalArcElementList) {
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(variantSymbol);
    Preconditions.checkNotNull(additionalArcElementList);

    this.parent = parent;
    this.name = parent.getName();
    this.enclosingScope = parent.getEnclosingScope();
    this.spannedScope = parent.getSpannedScope();
    this.symbol = Optional.of(variantSymbol);
    this.componentInstances = parent.getComponentInstanceList();
    this.head = parent.getHead();
    set_SourcePositionStart(parent.get_SourcePositionStart());
    set_SourcePositionEnd(parent.get_SourcePositionEnd());

    List<ASTArcElement> arcElementList = new ArrayList<>(parent.getBody().getArcElementList());
    arcElementList.addAll(additionalArcElementList);
    ASTVariantBuilder builder = new ASTVariantBuilder(variantSymbol);
    arcElementList = arcElementList.stream().map(builder::duplicate).collect(Collectors.toList());
    this.body = VariableArcMill.componentBodyBuilder().setArcElementsList(arcElementList).build();
  }

}
