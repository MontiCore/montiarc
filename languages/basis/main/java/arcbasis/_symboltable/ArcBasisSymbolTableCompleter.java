/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import arcbasis.check.ISynthesizeComponent;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.Timing;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;
import java.util.Optional;

public class ArcBasisSymbolTableCompleter implements ArcBasisVisitor2, ArcBasisHandler {

  protected CompTypeExpression currentCompInstanceType;
  protected ArcBasisTraverser traverser;
  protected IArcTypeCalculator typeCalculator;
  protected ISynthesizeComponent componentSynthesizer;
  protected ASTMCType currentPortType;
  protected ASTMCType currentFieldType;

  public ArcBasisSymbolTableCompleter() {
    this(new ArcBasisSynthesizeComponent(), new ArcBasisTypeCalculator());
  }

  public ArcBasisSymbolTableCompleter(@NotNull ISynthesizeComponent componentSynthesizer,
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
  public void visit(@NotNull ASTPortAccess node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    // If the port access has a component part,
    // then link the port access with the respective subcomponent
    if (node.isPresentComponent()) {
      node.getEnclosingScope()
        .resolveComponentInstanceMany(node.getComponent())
        .stream()
        .findFirst()
        .ifPresent(node::setComponentSymbol);
    }
    // Link the port access with the respective port
    // If the port access has a component part,
    // then the port belongs to a subcomponent
    if (node.isPresentComponent()) {
      if (node.isPresentComponentSymbol()
        && node.getComponentSymbol().isPresentType()
        && node.getComponentSymbol().getType().getTypeInfo() != null
        && node.getComponentSymbol()
        .getType().getTypeInfo().getEnclosingScope() != null) {
        node.getComponentSymbol()
          .getType()
          .getTypeInfo()
          .getSpannedScope()
          .resolvePortMany(node.getPort())
          .stream()
          .findFirst()
          .ifPresent(node::setPortSymbol);
      }
      // else the port belongs to this component
    } else {
      node.getEnclosingScope()
        .resolvePortMany(node.getPort())
        .stream()
        .findFirst()
        .ifPresent(node::setPortSymbol);
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
      String name = node.getMCType().toString();
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
    List<ASTPort> ports = node.getPortList();
    node.getTiming().ifPresent(t -> this.setTiming(ports, t));
    if (node.hasDelay()) this.setHasDelay(ports);
  }

  protected void setTiming(@NotNull List<ASTPort> ports, @NotNull Timing timing) {
    Preconditions.checkNotNull(ports);
    Preconditions.checkNotNull(timing);
    for (ASTPort port : ports) {
      this.setTiming(port, timing);
    }
  }

  protected void setTiming(@NotNull ASTPort port, @NotNull Timing timing) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(timing);
    Preconditions.checkArgument(port.isPresentSymbol());
    port.getSymbol().setTiming(timing);
  }

  protected void setHasDelay(@NotNull List<ASTPort> ports) {
    Preconditions.checkNotNull(ports);
    for (ASTPort port : ports) {
      this.setHasDelay(port);
    }
  }

  protected void setHasDelay(@NotNull ASTPort port) {
    Preconditions.checkNotNull(port);
    Preconditions.checkArgument(port.isPresentSymbol());
    port.getSymbol().setDelayed(true);
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(this.getCurrentPortType().get().equals(node.getMCType()));
    this.setCurrentPortType(null);
  }

  @Override
  public void visit(@NotNull ASTPort port) {
    Preconditions.checkNotNull(port);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(port.isPresentSymbol());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(this.getCurrentPortType().get());
      if (typeExpr.isPresentResult()) {
        port.getSymbol().setType(typeExpr.getResult());
      }
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      String name = this.getCurrentPortType().get().toString();
      if (this.getCurrentPortType().get() instanceof ASTMCQualifiedType) {
        name = ((ASTMCQualifiedType) this.getCurrentPortType().get()).getMCQualifiedName().getQName();
      }
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(name), this.getCurrentPortType().get().get_SourcePositionStart());
    }
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
      String name = this.getCurrentFieldType().get().toString();
      if (this.getCurrentFieldType().get() instanceof ASTMCQualifiedType) {
        name = ((ASTMCQualifiedType) this.getCurrentFieldType().get()).getMCQualifiedName().getQName();
      }
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(name), this.getCurrentFieldType().get().get_SourcePositionStart());
    }
  }
}