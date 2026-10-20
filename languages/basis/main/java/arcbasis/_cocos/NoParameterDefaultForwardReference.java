/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcParameter;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static montiarc.util.ArcError.PARAMETER_DEFAULT_FORWARD_REFERENCE;

/**
 * Checks that the default value of a parameter does not reference the parameter
 * itself or a parameter preceding it.
 */
public class NoParameterDefaultForwardReference implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    List<ASTArcParameter> parameters = node.getHead().getArcParameterList();
    Set<String> declared = new HashSet<>();
    Set<String> following = parameters.stream().map(ASTArcParameter::getName).collect(Collectors.toSet());
    for (ASTArcParameter parameter : parameters) {
      if (parameter.isPresentDefault()) {
        for (ASTNameExpression reference : this.namesIn(parameter)) {
          if (following.contains(reference.getName()) && !declared.contains(reference.getName())) {
            Log.error(PARAMETER_DEFAULT_FORWARD_REFERENCE.format(parameter.getName(), reference.getName()),
              reference.get_SourcePositionStart(), reference.get_SourcePositionEnd());
          }
        }
      }
      declared.add(parameter.getName());
    }
  }

  protected List<ASTNameExpression> namesIn(@NotNull ASTArcParameter parameter) {
    List<ASTNameExpression> names = new ArrayList<>();
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ExpressionsBasis(new ExpressionsBasisVisitor2() {
      @Override
      public void visit(ASTNameExpression node) {
        names.add(node);
      }
    });
    parameter.getDefault().accept(traverser);
    return names;
  }
}
