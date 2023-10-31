/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class ArcBasisScopesGenitorP2 implements ArcBasisVisitor2, ArcBasisHandler {

  protected CompTypeExpression currentCompInstanceType;
  protected ArcBasisTraverser traverser;
  protected IArcTypeCalculator typeCalculator;
  protected ISynthesizeComponent componentSynthesizer;

  public ArcBasisScopesGenitorP2() {
    this(new ArcBasisSynthesizeComponent(), new ArcBasisTypeCalculator());
  }

  public ArcBasisScopesGenitorP2(@NotNull ISynthesizeComponent componentSynthesizer,
                                 @NotNull IArcTypeCalculator typeCalculator) {
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  protected Optional<CompTypeExpression> getCurrentCompInstanceType() {
    return Optional.ofNullable((this.currentCompInstanceType));
  }

  protected void setCurrentCompInstanceType(@Nullable CompTypeExpression currentCompInstanceType) {
    this.currentCompInstanceType = currentCompInstanceType;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull ArcBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  public ISynthesizeComponent getComponentSynthesizer() {
    return this.componentSynthesizer;
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope().isPresentSpanningSymbol());
    Preconditions.checkArgument(node.getEnclosingScope().getSpanningSymbol() instanceof ComponentTypeSymbol);

    if (!node.isEmptyArcParents()) {
      ComponentTypeSymbol comp = (ComponentTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
      ImmutableList.Builder<CompTypeExpression> listBuilder = ImmutableList.builder();
      for (ASTArcParent astParent : node.getArcParentList()) {
        Optional<CompTypeExpression> parent = this.getComponentSynthesizer().synthesizeFrom(astParent.getType());
        if (parent.isPresent()) {
          astParent.getType().setDefiningSymbol(parent.get().getTypeInfo());
          listBuilder.add(parent.get());
          if (!astParent.isEmptyArcArguments()) {
            comp.setParentConfigurationExpressions(parent.get(), astParent.getArcArgumentList());
          }
        }
      }
      comp.setParentsList(listBuilder.build());
    }
  }

  @Override
  public void visit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);

    Optional<CompTypeExpression> comp = this.getComponentSynthesizer().synthesizeFrom(node.getMCType());
    if (comp.isPresent()) {
      node.getMCType().setDefiningSymbol(comp.get().getTypeInfo());
      this.setCurrentCompInstanceType(comp.get());
    }
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    this.setCurrentCompInstanceType(null);
  }

  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (this.getCurrentCompInstanceType().isPresent()) {
      node.getSymbol().setType(this.getCurrentCompInstanceType().get().deepClone());
      if (node.isPresentArcArguments()) {
        node.getSymbol().getType().addArcArguments(node.getArcArguments().getArcArgumentList());
      }
    }
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    Preconditions.checkState(node.isPresentSymbol());

    node.getSymbol().setType(this.getTypeCalculator().typeOf(node.getMCType()));
  }

  @Override
  public void visit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    SymTypeExpression type = this.getTypeCalculator().typeOf(node.getMCType());
    Timing timing = node.getTiming().orElse(null);

    for (ASTArcPort port : node.getArcPortList()) {
      port.getSymbol().setType(type);
      port.getSymbol().setTiming(timing);
      if (node.hasDelay()) port.getSymbol().setDelayed(true);
    }
  }

  @Override
  public void visit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    SymTypeExpression type = this.getTypeCalculator().typeOf(node.getMCType());

    for (ASTArcField field : node.getArcFieldList()) {
      field.getSymbol().setType(type);
    }
  }
}
