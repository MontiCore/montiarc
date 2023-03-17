/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos.util;

import arcautomaton._visitor.NamesInExpressionsVisitor;
import arcautomaton._visitor.StatechartNameResolver;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class FieldReadWriteAccessFitsInExpressions {

  /**
   * Used to find instances of variable names and the kind of their access
   */
  protected final NamesInExpressionsVisitor visitor;

  protected final ITraverser traverser;

  public FieldReadWriteAccessFitsInExpressions(@NotNull NamesInExpressionsVisitor visitor,
                                               @NotNull ITraverser traverser) {
    this.visitor = Preconditions.checkNotNull(visitor);
    this.traverser = Preconditions.checkNotNull(traverser);
    visitor.registerTo(traverser);
  }

  /**
   * Ensures that referenced variables/ports are read/written correctly.
   * For example, the values of outgoing ports are unknown and may therefore not be read
   *
   * @param expression root of the expression-tree to check
   */
  public void checkVarAccessIn(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);

    visitor.reset();
    expression.accept(traverser);
    visitor.getFoundNames().forEach(FieldReadWriteAccessFitsInExpressions::checkVariableAccess);
  }

  /**
   * Analyzes name-expressions and ensures that names,
   * which correspond to final variables or input ports, are not written to.
   * Similarly, names which reference outgoing ports may not be used to read from.
   * Instances of invalid access are {@link de.se_rwth.commons.logging.Log#error(String) logged}.
   *
   * @param nameExpr ASTExpression referring to a variable / port that whose value should be accessed.
   * @param accessKind Desired access-action that should be checked for permission.
   */
  protected static void checkVariableAccess(@NotNull ASTNameExpression nameExpr,
                                            @NotNull NamesInExpressionsVisitor.VarAccessKind accessKind){
    Preconditions.checkNotNull(nameExpr);
    Preconditions.checkNotNull(accessKind);
    Preconditions.checkNotNull(nameExpr.getEnclosingScope());

    String accessedName = nameExpr.getName();

    StatechartNameResolver resolver = new StatechartNameResolver(nameExpr.getEnclosingScope());
    String component = resolver.getName();
    Optional<PortSymbol> port = resolver.resolvePort(accessedName);
    Optional<VariableSymbol> field = resolver.resolveField(accessedName);

    if(port.isPresent()){
      checkPortAccess(port.get(), accessKind, nameExpr, component);
    } else if (field.isPresent()) {
      checkVarSymAccess(field.get(), accessKind, nameExpr, component);
    } else {
      Log.debug(String.format("Not checking read / write access on '%s' at '%s', as there was no variable / port " +
        "found with that name", accessedName, nameExpr.get_SourcePositionStart()),
        FieldReadWriteAccessFitsInExpressions.class.getName());
    }
  }

  /**
   * Checks whether the access defined by {@code accessKind} is allowed on {@code port}.
   * @param port See method description.
   * @param accessKind See method description.
   * @param astLocation The ast node of the {@link ASTNameExpression} which refers to the accessed variable. Needed for
   *                    error logging.
   * @param enclCompTypeName The name of the component type in which the variable is defined. Needed for error logging.
   */
  protected static void checkPortAccess(@NotNull PortSymbol port,
                                        @NotNull NamesInExpressionsVisitor.VarAccessKind accessKind,
                                        @NotNull ASTNameExpression astLocation,
                                        @NotNull String enclCompTypeName) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(accessKind);
    Preconditions.checkNotNull(astLocation);
    Preconditions.checkNotNull(enclCompTypeName);

    if(port.isIncoming() && accessKind.performsMutation()){
      Log.error(ArcError.WRITE_TO_INCOMING_PORT.format(port.getName(), enclCompTypeName),
        astLocation.get_SourcePositionStart(), astLocation.get_SourcePositionEnd()
      );
    } else if(port.isOutgoing() && accessKind.includesRead()) {
      Log.error(ArcError.READ_FROM_OUTGOING_PORT.format(port.getName(), enclCompTypeName),
        astLocation.get_SourcePositionStart(), astLocation.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks whether the access defined by {@code accessKind} is allowed on {@code variable}.
   * @param variable See method description.
   * @param accessKind See method description.
   * @param astLocation The ast node of the {@link ASTNameExpression} which refers to the accessed variable. Needed for
   *                    error logging.
   * @param enclCompTypeName The name of the component type in which the variable is defined. Needed for error logging.
   */
  protected static void checkVarSymAccess(@NotNull VariableSymbol variable,
                                          @NotNull NamesInExpressionsVisitor.VarAccessKind accessKind,
                                          @NotNull ASTNameExpression astLocation,
                                          @NotNull String enclCompTypeName) {
    Preconditions.checkNotNull(variable);
    Preconditions.checkNotNull(accessKind);
    Preconditions.checkNotNull(astLocation);
    Preconditions.checkNotNull(enclCompTypeName);

    if(variable.isIsReadOnly() && accessKind.performsMutation()) {
      Log.error(ArcError.WRITE_TO_READONLY_VARIABLE.format(variable.getName(), enclCompTypeName),
        astLocation.get_SourcePositionStart(), astLocation.get_SourcePositionEnd()
      );
    }
  }
}
