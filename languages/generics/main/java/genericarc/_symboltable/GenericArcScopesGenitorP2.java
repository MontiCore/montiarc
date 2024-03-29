/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis._ast.ASTArcParent;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import genericarc._visitor.GenericArcHandler;
import genericarc._visitor.GenericArcTraverser;
import genericarc._visitor.GenericArcVisitor2;
import genericarc.check.GenericArcTypeCalculator;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class GenericArcScopesGenitorP2 implements GenericArcVisitor2, GenericArcHandler {

  protected GenericArcTraverser traverser;
  protected ISynthesizeComponent componentSynthesizer;
  protected IArcTypeCalculator typeCalculator;

  public GenericArcScopesGenitorP2() {
    this(new ArcBasisSynthesizeComponent(), new GenericArcTypeCalculator());
  }

  public GenericArcScopesGenitorP2(@NotNull ISynthesizeComponent componentSynthesizer,
                                   @NotNull IArcTypeCalculator typeCalculator) {
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  @Override
  public GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull GenericArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public ISynthesizeComponent getComponentSynthesizer() {
    return this.componentSynthesizer;
  }

  public void setComponentSynthesizer(@NotNull ISynthesizeComponent componentSynthesizer) {
    Preconditions.checkNotNull(componentSynthesizer);
    this.componentSynthesizer = componentSynthesizer;
  }

  public IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  @Override
  public void visit(@NotNull ASTGenericComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope().isPresentSpanningSymbol());
    Preconditions.checkArgument(node.getEnclosingScope().getSpanningSymbol() instanceof ComponentTypeSymbol);

    if (!node.isEmptyArcParents()) {
      ComponentTypeSymbol comp = (ComponentTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
      ImmutableList.Builder<CompKindExpression> listBuilder = ImmutableList.builder();
      for (ASTArcParent astParent : node.getArcParentList()) {
        Optional<CompKindExpression> parent = this.getComponentSynthesizer().synthesizeFrom(astParent.getType());
        if (parent.isPresent()) {
          astParent.getType().setDefiningSymbol(parent.get().getTypeInfo());
          listBuilder.add(parent.get());
          if (!astParent.isEmptyArcArguments() && parent.get() instanceof CompTypeExpression) {
            ((CompTypeExpression) parent.get()).addArcArguments(astParent.getArcArgumentList());
            parent.get().bindParams();
          }
        }
      }
      comp.setSuperComponentsList(listBuilder.build());
    }
  }

  @Override
  public void visit(@NotNull ASTArcTypeParameter typeParam) {
    Preconditions.checkNotNull(typeParam);
    Preconditions.checkArgument(typeParam.isPresentSymbol());

    TypeVarSymbol typeParamSym = typeParam.getSymbol();

    for (ASTMCType upperBound : typeParam.getUpperBoundList()) {
      try {
        typeParamSym.addSuperTypes(this.getTypeCalculator().typeOf(upperBound));
      }  catch (ResolvedSeveralEntriesForSymbolException e) {
        Log.error(ArcError.AMBIGUOUS_REFERENCE.format(GenericArcMill.prettyPrint(upperBound, false)), upperBound.get_SourcePositionStart());
      }
    }
  }
}