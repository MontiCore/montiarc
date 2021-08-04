/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;
import java.util.Stack;

public class ArcBasisSymbolTableCompleter implements ArcBasisVisitor2, ArcBasisHandler {

  protected MCBasicTypesFullPrettyPrinter typePrinter;

  public void setTypePrinter(@NotNull MCBasicTypesFullPrettyPrinter typesPrinter) {
    Preconditions.checkNotNull(typesPrinter);
    this.typePrinter = typesPrinter;
  }

  protected String printType(@NotNull ASTMCType type) {
    Preconditions.checkNotNull(type);
    return type.printType(typePrinter);
  }

  protected Stack<ComponentTypeSymbol> componentStack = new Stack<>();

  protected Stack<ComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
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

  public ArcBasisSymbolTableCompleter() {
    this.typePrinter = MCBasicTypesMill.mcBasicTypesPrettyPrinter();
  }

  public ArcBasisSymbolTableCompleter(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    Preconditions.checkNotNull(typePrinter);
    this.typePrinter = typePrinter;
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    this.putOnStack(node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getAstNode().equals(node));
    this.removeCurrentComponent();
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    if (node.isPresentParent()) {
      String type;
      if (node.getParent() instanceof ASTMCGenericType) {
        type = ((ASTMCGenericType) node.getParent()).printWithoutTypeArguments();
       } else {
        type = this.printType(node.getParent());
      }
      Optional<ComponentTypeSymbol> parent = node.getEnclosingScope().resolveComponentType(type);
      if (!parent.isPresent()) {
        Log.error(ArcError.SYMBOL_NOT_FOUND.format(type), node.get_SourcePositionStart());
      } else {
        this.getCurrentComponent().get().setParent(parent.get());
      }
    }
  }

  protected ArcBasisTraverser traverser ;

  @Override
  public void setTraverser(@NotNull ArcBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return this.traverser;
  }
}