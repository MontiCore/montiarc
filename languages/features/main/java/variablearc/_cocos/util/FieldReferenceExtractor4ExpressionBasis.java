/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class FieldReferenceExtractor4ExpressionBasis
  implements ExpressionsBasisHandler, IFieldReferenceInExpressionExtractor {

  protected LinkedHashSet<FieldReference> fieldReferencesToLookFor;
  protected final LinkedHashMap<FieldReference, SourcePosition> foundFieldReferences;
  protected ExpressionsBasisTraverser traverser;

  public FieldReferenceExtractor4ExpressionBasis() {
    this(ExpressionsBasisMill.traverser());
  }

  public FieldReferenceExtractor4ExpressionBasis(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.fieldReferencesToLookFor = new LinkedHashSet<>();
    this.foundFieldReferences = new LinkedHashMap<>();
    this.setTraverser(traverser);
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public LinkedHashMap<FieldReference, SourcePosition> getFoundFieldReferences() {
    return this.foundFieldReferences;
  }

  public void clearFoundFieldReferences() {
    this.foundFieldReferences.clear();
  }

  public LinkedHashSet<FieldReference> getFieldReferencesToLookFor() {
    return this.fieldReferencesToLookFor;
  }

  protected void setFieldReferencesToLookFor(@NotNull LinkedHashSet<FieldReference> fieldReferencesToLookFor) {
    Preconditions.checkNotNull(fieldReferencesToLookFor);
    this.fieldReferencesToLookFor = fieldReferencesToLookFor;
  }

  @Override
  public void handle(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);

    this.getFieldReferencesToLookFor().stream()
      .filter(pr -> pr.getFieldName().equals(expr.getName()))
      .forEach(pr -> foundFieldReferences.put(pr, expr.get_SourcePositionStart()));
  }

  @Override
  public LinkedHashMap<FieldReference, SourcePosition> findFieldReferences(@NotNull ASTExpression expr,
                                                                     @NotNull LinkedHashSet<FieldReference> fieldReferencesToLookFor,
                                                                     @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(fieldReferencesToLookFor);
    Preconditions.checkNotNull(traverser);
    Preconditions.checkArgument(traverser instanceof ExpressionsBasisTraverser);

    this.clearFoundFieldReferences();
    this.setFieldReferencesToLookFor(fieldReferencesToLookFor);
    this.setTraverser((ExpressionsBasisTraverser) traverser);

    this.getTraverser().setExpressionsBasisHandler(this);
    expr.accept(traverser);

    return this.getFoundFieldReferences();
  }
}
