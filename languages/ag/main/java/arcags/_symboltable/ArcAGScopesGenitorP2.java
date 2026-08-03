/* (c) https://github.com/MontiCore/monticore */
package arcags._symboltable;

import arcags._ast.ASTArcAG;
import arcags._visitor.ArcAGsVisitor2;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.streams.StreamSymTypeFactory;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;
import java.util.Stack;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;

public class ArcAGScopesGenitorP2 implements ArcAGsVisitor2, ArcBasisVisitor2 {

  protected Stack<ASTArcComponentType> componentStack;

  public ArcAGScopesGenitorP2() {
    super();
    this.componentStack = new Stack<>();
  }

  protected Stack<ASTArcComponentType> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<ASTArcComponentType> getCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().peek());
  }

  protected void removeCurrentComponent() {
    this.getComponentStack().pop();
  }

  protected void putOnStack(@Nullable ASTArcComponentType symbol) {
    this.getComponentStack().push(symbol);
  }

  @Override
  public void visit(@NotNull ASTArcAG node) {
    Preconditions.checkNotNull(node);

    Optional<ASTArcComponentType> optional = getCurrentComponent();
    if (optional.isEmpty()) {
      Log.debug(() ->
          "Assumption Guarantee. Skip creation of inner scope due to missing component scope",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd(),
          this.getClass().getSimpleName()
      );
      return;
    }
    ASTArcComponentType astNode = optional.get();
    if (!astNode.isPresentSymbol()) {
      Log.debug(() ->
          "Assumption Guarantee. Skip creation of inner scope due to missing component symbol",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd(),
        this.getClass().getSimpleName()
      );
      return;
    }
    ComponentTypeSymbol comp = astNode.getSymbol();
    for (PortSymbol port : comp.getPorts()) {
      SymTypeExpression streamtype;
      if (port.getTiming() == TIMED_SYNC) {
        streamtype = StreamSymTypeFactory.createSyncStream(port.getType());
      } else {
        streamtype = StreamSymTypeFactory.createEventStream(port.getType());
      }

      VariableSymbol variableStream = new VariableSymbolBuilder()
        .setEnclosingScope(port.getEnclosingScope())
        .setName(port.getName())
        .setType(streamtype)
        .setAstNodeAbsent()
        .setFullName(port.getFullName())
        .setIsReadOnly(true)
        .setPackageName(port.getPackageName())
        .build();
      node.getSpannedScope().add(variableStream);
    }
  }

  @Override
  public void endVisit(ASTArcComponentType node) {
    removeCurrentComponent();
  }

  @Override
  public void visit(ASTArcComponentType node) {
    putOnStack(node);
  }
}
