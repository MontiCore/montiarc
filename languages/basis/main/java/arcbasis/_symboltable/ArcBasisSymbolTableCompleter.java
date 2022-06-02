/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.*;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.check.*;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class ArcBasisSymbolTableCompleter implements ArcBasisVisitor2, ArcBasisHandler {

  protected MCBasicTypesFullPrettyPrinter typePrinter;
  protected CompTypeExpression currentCompInstanceType;
  protected ArcBasisTraverser traverser;
  protected IArcTypeCalculator typeCalculator;
  protected ISynthesizeComponent componentSynthesizer;
  protected ASTMCType currentPortType;
  protected ASTMCType currentFieldType;

  public ArcBasisSymbolTableCompleter() {
    this(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  public ArcBasisSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    this(typePrinter, new ArcBasisSynthesizeComponent(), new ArcBasisTypeCalculator());
  }

  public ArcBasisSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter,
                                      @NotNull ISynthesizeComponent componentSynthesizer,
                                      @NotNull IArcTypeCalculator typeCalculator) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
    this.componentSynthesizer = Preconditions.checkNotNull(componentSynthesizer);
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  public MCBasicTypesFullPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull MCBasicTypesFullPrettyPrinter typesPrinter) {
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
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getMCType());
    Preconditions.checkState(node.isPresentSymbol(), "Missing symbol for configuration parameter '%s' at %s. " +
        "Did you forget to run the scopes genitor prior to the completer?", node.getName(),
      node.get_SourcePositionStart());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(node.getMCType());
      if (typeExpr.isPresentCurrentResult()) {
        node.getSymbol().setType(typeExpr.getCurrentResult());
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
    Preconditions.checkState(port.isPresentSymbol(),
        "Missing symbol for port '%s' at %s. Did you forget to run the " + "scopes genitor prior to the completer?", port.getName(),
        port.get_SourcePositionStart());

    try {
      TypeCheckResult typeExpr = this.getTypeCalculator().synthesizeType(this.getCurrentPortType().get());
      if (typeExpr.isPresentCurrentResult()) {
        port.getSymbol().setType(typeExpr.getCurrentResult());
      }
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      String name = "";
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
      if (typeExpr.isPresentCurrentResult()) {
        field.getSymbol().setType(typeExpr.getCurrentResult());
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