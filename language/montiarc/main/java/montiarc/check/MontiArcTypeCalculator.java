/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisTraverser;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.DeriveSymTypeOfExpressionWithPortsAndFeatures;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcTypeCalculator extends AbstractArcTypeCalculator {

  public MontiArcTypeCalculator() {
    this(new TypeCheckResult());
  }

  public MontiArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult) {
    this(typeCheckResult, MontiArcMill.traverser());
  }

  protected MontiArcTypeCalculator(@NotNull TypeCheckResult typeCheckResult,
                                   @NotNull MontiArcTraverser traverser) {
    super(typeCheckResult, traverser);
    this.init(traverser);
  }

  protected void init(@NotNull MontiArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.initDeriveSymTypeOfLiterals(traverser);
    this.initDeriveSymTypeOfExpression(traverser);
    this.initDeriveSymTypeOfMCCommonLiterals(traverser);
    this.initDeriveSymTypeOfCommonExpressions(traverser);
    this.initDeriveSymTypeOfAssignmentExpressions(traverser);
    this.initSynthesizeSymTypeFromMCBasicTypes(traverser);
    this.initSynthesizeSymTypeFromMCCollectionTypes(traverser);
    this.initSynthesizeSymTypeFromMCSimpleGenericTypes(traverser);
  }

  protected void initDeriveSymTypeOfLiterals(@NotNull MCLiteralsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }

  protected void initDeriveSymTypeOfExpression(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionWithPortsAndFeatures();
    deriveSymTypeOfExpression.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
  }

  protected void initDeriveSymTypeOfMCCommonLiterals(@NotNull MCCommonLiteralsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
  }

  protected void initDeriveSymTypeOfCommonExpressions(@NotNull CommonExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    ArcDeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new ArcDeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
  }

  protected void initDeriveSymTypeOfAssignmentExpressions(@NotNull AssignmentExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions =
      new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);
  }

  protected void initSynthesizeSymTypeFromMCBasicTypes(@NotNull MCBasicTypesTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    SynthesizeSymTypeFromMCBasicTypes mCBasicTypesVisitor = new SynthesizeSymTypeFromMCBasicTypes();
    mCBasicTypesVisitor.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCBasicTypes(mCBasicTypesVisitor);
    traverser.setMCBasicTypesHandler(mCBasicTypesVisitor);
  }

  protected void initSynthesizeSymTypeFromMCCollectionTypes(@NotNull MCCollectionTypesTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    SynthesizeSymTypeFromMCCollectionTypes synCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synCollectionTypes.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCCollectionTypes(synCollectionTypes);
    traverser.setMCCollectionTypesHandler(synCollectionTypes);
  }

  protected void initSynthesizeSymTypeFromMCSimpleGenericTypes(@NotNull MCSimpleGenericTypesTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    SynthesizeSymTypeFromMCSimpleGenericTypes synSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synSimpleGenericTypes.setTypeCheckResult(this.getTypeCheckResult());
    traverser.add4MCSimpleGenericTypes(synSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synSimpleGenericTypes);
  }
}