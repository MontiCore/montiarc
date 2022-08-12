/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.*;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;
import java.util.Optional;

public class ArcBasisSymbolTableCompleter implements ArcBasisVisitor2, ArcBasisHandler {

  protected IFullPrettyPrinter typePrinter;
  protected CompTypeExpression currentCompInstanceType;
  protected ArcBasisTraverser traverser;
  protected IArcTypeCalculator typeCalculator;
  protected ISynthesizeComponent componentSynthesizer;
  protected ASTMCType currentPortType;
  protected List<Timing> currentPortTimings;
  protected ASTMCType currentFieldType;

  public ArcBasisSymbolTableCompleter() {
    this(ArcBasisMill.fullPrettyPrinter());
  }

  public ArcBasisSymbolTableCompleter(@NotNull IFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent(), new ArcBasisTypeCalculator());
  }

  public ArcBasisSymbolTableCompleter(@NotNull IFullPrettyPrinter typePrinter,
                                      @NotNull ISynthesizeComponent componentSynthesizer,
                                      @NotNull IArcTypeCalculator typeCalculator) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  public IFullPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull IFullPrettyPrinter typesPrinter) {
    Preconditions.checkNotNull(typesPrinter);
    this.typePrinter = typesPrinter;
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

  public void setComponentSynthesizer(@NotNull ISynthesizeComponent componentSynthesizer) {
    Preconditions.checkNotNull(componentSynthesizer);
    this.componentSynthesizer = componentSynthesizer;
  }

  protected Optional<ASTMCType> getCurrentPortType() {
    return Optional.ofNullable(this.currentPortType);
  }

  protected void setCurrentPortType(@Nullable ASTMCType currentPortType) {
    this.currentPortType = currentPortType;
  }

  protected Optional<List<Timing>> getCurrentPortTimings() {
    return Optional.ofNullable(this.currentPortTimings);
  }

  protected void setCurrentPortTimings(@Nullable List<Timing> currentPortTimings) {
    this.currentPortTimings = currentPortTimings;
  }

  protected Optional<ASTMCType> getCurrentFieldType() {
    return Optional.ofNullable(this.currentFieldType);
  }

  protected void setCurrentFieldType(@Nullable ASTMCType currentFieldType) {
    this.currentFieldType = currentFieldType;
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getComponentInstanceList().isEmpty()) {
      this.setCurrentCompInstanceType(typeExprForDirectComponentInstantiation(node));
    }
  }

  protected CompTypeExpression typeExprForDirectComponentInstantiation(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    if (!node.getSymbol().getTypeParameters().isEmpty()) {
      Log.error(ArcError.GENERIC_COMPONENT_TYPE_INSTANTIATION.format(
              node.getSymbol().getName(),
              node.getSymbol().getTypeParameters().stream().map(ISymbol::getName).reduce("", (a, b) -> a + "'" + b + "' ")),
              node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    return new TypeExprOfComponent(node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.setCurrentCompInstanceType(null);
    ArcBasisDelayedPortPropagation.complete(node);
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope().isPresentSpanningSymbol());
    Preconditions.checkArgument(node.getEnclosingScope().getSpanningSymbol() instanceof ComponentTypeSymbol);

    if (node.isPresentParent()) {
      Optional<CompTypeExpression> parentTypeExpr = this.getComponentSynthesizer().synthesizeFrom(node.getParent());
      if (parentTypeExpr.isPresent()) {
        ComponentTypeSymbol sym = (ComponentTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
        sym.setParent(parentTypeExpr.get());
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
      node.getSymbol().setType(this.getCurrentCompInstanceType().get());
    }
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    Preconditions.checkState(node.isPresentSymbol(), "Missing symbol for configuration parameter '%s' at %s. " +
        "Did you forget to run the scopes genitor prior to the completer?", node.getName(),
      node.get_SourcePositionStart());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(node.getMCType());
      if (typeExpr.isPresentResult()) {
        node.getSymbol().setType(typeExpr.getResult());
      }
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      String name = "";
      if (node.getMCType() instanceof ASTMCQualifiedType) {
        name = ((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName();
      }
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(name), node.getMCType().get_SourcePositionStart());
    }
  }

  @Override
  public void visit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    this.setCurrentPortType(node.getMCType());
    this.setCurrentPortTimings(node.getTimings());
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(this.getCurrentPortType().get().equals(node.getMCType()));
    this.setCurrentPortType(null);
    this.setCurrentPortTimings(null);
  }

  @Override
  public void visit(@NotNull ASTPort port) {
    Preconditions.checkNotNull(port);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(port.isPresentSymbol(),
        "Missing symbol for port '%s' at %s. Did you forget to run the " + "scopes genitor prior to the completer?", port.getName(),
        port.get_SourcePositionStart());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(this.getCurrentPortType().get());
      if (typeExpr.isPresentResult()) {
        port.getSymbol().setType(typeExpr.getResult());
      }
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      String name = "";
      if (this.getCurrentPortType().get() instanceof ASTMCQualifiedType) {
        name = ((ASTMCQualifiedType) this.getCurrentPortType().get()).getMCQualifiedName().getQName();
      }
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(name), this.getCurrentPortType().get().get_SourcePositionStart());
    }
    Timing timing = this.getCurrentPortTimings().flatMap(o -> o.stream().findFirst()).orElseGet(Timing::untimed);
    port.getSymbol().setTiming(timing);
  }

  @Override
  public void visit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    this.setCurrentFieldType(node.getMCType());
  }

  @Override
  public void endVisit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().get().equals(node.getMCType()));
    this.setCurrentFieldType(null);
  }

  @Override
  public void visit(@NotNull ASTArcField field) {
    Preconditions.checkNotNull(field);
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    Preconditions.checkState(field.isPresentSymbol(), "Missing symbol for field '%s' at %s. Did you forget to run " +
      "the scopes genitor prior to the completer?", field.getName(), field.get_SourcePositionStart());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(this.getCurrentFieldType().get());
      if (typeExpr.isPresentResult()) {
        field.getSymbol().setType(typeExpr.getResult());
      }
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      String name = "";
      if (this.getCurrentFieldType().get() instanceof ASTMCQualifiedType) {
        name = ((ASTMCQualifiedType) this.getCurrentFieldType().get()).getMCQualifiedName().getQName();
      }
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(name), this.getCurrentFieldType().get().get_SourcePositionStart());
    }
  }
}