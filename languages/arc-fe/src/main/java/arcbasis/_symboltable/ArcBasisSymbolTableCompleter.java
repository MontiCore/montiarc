/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.ISynthesizeComponent;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;
import java.util.Stack;

public class ArcBasisSymbolTableCompleter implements ArcBasisVisitor2, ArcBasisHandler {

  protected MCBasicTypesFullPrettyPrinter typePrinter;

  public MCBasicTypesFullPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull MCBasicTypesFullPrettyPrinter typesPrinter) {
    Preconditions.checkNotNull(typesPrinter);
    this.typePrinter = typesPrinter;
  }

  protected Stack<ComponentTypeSymbol> componentStack;

  protected Stack<ComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected void setComponentStack(@NotNull Stack<ComponentTypeSymbol> stack) {
    Preconditions.checkNotNull(stack);
    Preconditions.checkArgument(!stack.contains(null));
    this.componentStack = stack;
  }

  protected Optional<ComponentTypeSymbol> getCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().peek());
  }

  protected Optional<ComponentTypeSymbol> removeCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().pop());
  }

  protected void putOnStack(@Nullable ComponentTypeSymbol symbol) {
    this.getComponentStack().push(symbol);
  }

  protected CompTypeExpression currentCompInstanceType;

  protected Optional<CompTypeExpression> getCurrentCompInstanceType() {
    return Optional.ofNullable((this.currentCompInstanceType));
  }

  protected void setCurrentCompInstanceType(@Nullable CompTypeExpression currentCompInstanceType) {
    this.currentCompInstanceType = currentCompInstanceType;
  }

  protected ArcBasisTraverser traverser;

  @Override
  public void setTraverser(@NotNull ArcBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  protected ISynthesize typeSynthesizer;

  protected void setTypeSynthesizer(@NotNull ISynthesize typeSynthesizer) {
    Preconditions.checkNotNull(typeSynthesizer);
    this.typeSynthesizer = typeSynthesizer;
  }

  public ISynthesize getTypeSynthesizer() {
    return this.typeSynthesizer;
  }

  protected ISynthesizeComponent componentSynthesizer;

  protected void setComponentSynthesizer(@NotNull ISynthesizeComponent componentSynthesizer) {
    Preconditions.checkNotNull(componentSynthesizer);
    this.componentSynthesizer = componentSynthesizer;
  }

  protected ISynthesizeComponent getComponentSynthesizer() {
    return this.componentSynthesizer;
  }

  public ArcBasisSymbolTableCompleter() {
    this(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  public ArcBasisSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent());
  }

  public ArcBasisSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter,
                                      @NotNull ISynthesizeComponent componentSynthesizer) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.componentStack = new Stack<>();
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.putOnStack(node.getSymbol());

    if (!node.getComponentInstanceList().isEmpty()) {
      this.setCurrentCompInstanceType(typeExprForDirectComponentInstantiation(node));
    }
  }

  protected CompTypeExpression typeExprForDirectComponentInstantiation(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().getTypeParameters().isEmpty(),
      "ArcBasis does not support generic components when instantiating component types within their" +
        " type declaration. But component type '%s' has type parameters %s. Occurrence: %s",
      node.getSymbol().getName(),
      node.getSymbol().getTypeParameters().stream().map(ISymbol::getName).reduce("", (a, b) -> a + "'" + b + "' "),
      node.get_SourcePositionStart());

    return new TypeExprOfComponent(node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getAstNode().equals(node));
    Preconditions.checkState(this.getCurrentComponent().get().equals(node.getSymbol()));
    this.removeCurrentComponent();
    this.setCurrentCompInstanceType(null);
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    if (node.isPresentParent()) {
      Optional<CompTypeExpression> parentTypeExpr = this.getComponentSynthesizer().synthesizeFrom(node.getParent());
      if (parentTypeExpr.isPresent()) {
        this.getCurrentComponent().get().setParent(parentTypeExpr.get());
      } else {
        Log.error(String.format("Could not create a component type expression from '%s'",
          node.getParent().printType(this.getTypePrinter())), node.get_SourcePositionStart()
        );
      }
    }
  }

  @Override
  public void visit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    Optional<CompTypeExpression> compTypeExpr = this.getComponentSynthesizer().synthesizeFrom(node.getMCType());
    if (compTypeExpr.isPresent()) {
      this.setCurrentCompInstanceType(compTypeExpr.get());
    } else {
      Log.error(String.format("Could not create a component type expression from '%s'",
        node.getMCType().printType(this.getTypePrinter())), node.get_SourcePositionStart()
      );
    }
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    this.setCurrentCompInstanceType(null);
  }

  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());

    node.getSymbol().setType(this.getCurrentCompInstanceType().get());
  }
}