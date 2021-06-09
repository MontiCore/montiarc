/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.NamePresenceChecker;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import arcbasis._symboltable.PortSymbol;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Map;
import java.util.Optional;

import static arcautomaton._visitor.NamesInExpressionsVisitor.ExpressionKind.*;

public class FieldReadWriteAccessFitsInGuards implements SCTransitions4CodeASTTransitionBodyCoCo {

  /**
   * used to find instances of variable names and the kind of their access
   */
  protected final NamesInExpressionsVisitor visitor;

  /**
   * default constructor that is sufficient for basic expression language components
   */
  public FieldReadWriteAccessFitsInGuards(){
    this(new NamesInExpressionsVisitor());
  }

  /**
   * This is a fully parameterized constructor that facilitates registering new expression types by passing another visitor
   * @param visitor {@link #visitor}
   */
  public FieldReadWriteAccessFitsInGuards(@NotNull NamesInExpressionsVisitor visitor) {
    this.visitor = Preconditions.checkNotNull(visitor);
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);
    if(node.isPresentPre()) {
      checkReferencesIn(node.getPre());
    }
  }

  /**
   * Ensures that referenced variables/ports are read/written correctly.
   * For example, the values of outgoing ports are unknown and may therefore not be read
   *
   * @param expression root of the expression-tree to check
   */
  protected void checkReferencesIn(@NotNull ASTExpression expression){
    Preconditions.checkNotNull(expression);
    ExpressionsBasisTraverser traverser = ArcAutomatonMill.inheritanceTraverser();
    visitor.reset();
    visitor.registerTo(traverser);
    expression.accept(traverser);
    checkVariableAccess(visitor.getFoundNames(), expression.getEnclosingScope());
  }

  /**
   * Analyzes name-expressions and ensures that names,
   * which correspond to final variables or input ports, are not written.
   * Similarly, names which reference outgoing ports may not be used to read from.
   * Instances of invalid access are {@link de.se_rwth.commons.logging.Log#error(String) logged}.
   *
   * @param map map that contains all found instances of variable access and the desired access-action.
   * @param scope scope in which the found expressions are located
   */
  protected static void checkVariableAccess(@NotNull Map<ASTNameExpression, NamesInExpressionsVisitor.ExpressionKind> map, @NotNull IScope scope){
    Preconditions.checkNotNull(map);
    Preconditions.checkNotNull(scope);
    NamePresenceChecker resolver = new NamePresenceChecker(scope);
    String component = scope.getName();

    map.forEach((node, access) -> {
      String name = node.getName();
      Optional<PortSymbol> port = resolver.resolvePort(name);
      if(port.isPresent()){
        if(port.get().isIncoming()){
          if(access == UPDATE || access == OVERWRITE){
            BehaviorError.WRITE_TO_INCOMING_PORT.logAt(node, name, component);
          }
        } else
          if(port.get().isOutgoing()){
            if(access == UPDATE || access == DEFAULT_READ){
              BehaviorError.READ_FROM_OUTGOING_PORT.logAt(node, name, component);
            }
          }
      } else {
        Optional<VariableSymbol> field = resolver.resolveField(name);
        if(field.isPresent() && field.get().isIsReadOnly()){
          if(access == UPDATE || access == OVERWRITE){
            BehaviorError.WRITE_TO_READONLY_VARIABLE.logAt(node, name, component);
          }
        }
      }
    });
  }
}