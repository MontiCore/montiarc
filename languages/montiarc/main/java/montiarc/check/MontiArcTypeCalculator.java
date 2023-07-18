/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcTypeCalculator extends AbstractArcTypeCalculator {

  public MontiArcTypeCalculator() {
    this(init(MontiArcMill.traverser()));
  }

  protected MontiArcTypeCalculator(@NotNull MontiArcTraverser t) {
    super(t);
  }

  protected static MontiArcTraverser init(@NotNull MontiArcTraverser t) {
    Preconditions.checkNotNull(t);
    initMCCommonLiteralsTypeVisitor(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    initBitExpressionsTypeVisitor(t);
    initCommonExpressionsTypeVisitor(t);
    initAssignmentExpressionsTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    initMCCollectionTypesTypeVisitor(t);
    initMCSimpleGenericTypesTypeVisitor(t);
    return t;
  }

  public static void initMCCommonLiteralsTypeVisitor(@NotNull MCCommonLiteralsTraverser t) {
    Preconditions.checkNotNull(t);
    MCCommonLiteralsTypeVisitor visitor = new MCCommonLiteralsTypeVisitor();
    t.add4MCCommonLiterals(visitor);
  }

  public static void initCommonExpressionsTypeVisitor(@NotNull CommonExpressionsTraverser t) {
    Preconditions.checkNotNull(t);
    MACommonExpressionsTypeVisitor visitor = new MACommonExpressionsTypeVisitor();
    visitor.setWithinTypeBasicSymbolsResolver(new MAOOWithinTypeBasicSymbolsResolver());
    t.add4CommonExpressions(visitor);
    t.setCommonExpressionsHandler(visitor);
  }

  public static void initAssignmentExpressionsTypeVisitor(@NotNull AssignmentExpressionsTraverser t) {
    Preconditions.checkNotNull(t);
    AssignmentExpressionsTypeVisitor visitor = new AssignmentExpressionsTypeVisitor();
    t.add4AssignmentExpressions(visitor);
  }

  public static void initBitExpressionsTypeVisitor(@NotNull BitExpressionsTraverser t) {
    Preconditions.checkNotNull(t);
    BitExpressionsTypeVisitor visitor = new BitExpressionsTypeVisitor();
    t.add4BitExpressions(visitor);
  }

  public static void initMCCollectionTypesTypeVisitor(@NotNull MCCollectionTypesTraverser t) {
    Preconditions.checkNotNull(t);
    MCCollectionTypesTypeVisitor visitor = new MCCollectionTypesTypeVisitor();
    t.add4MCCollectionTypes(visitor);
  }

  public static void initMCSimpleGenericTypesTypeVisitor(@NotNull MCSimpleGenericTypesTraverser t) {
    Preconditions.checkNotNull(t);
    MCSimpleGenericTypesTypeVisitor visitor = new MCSimpleGenericTypesTypeVisitor();
    t.add4MCSimpleGenericTypes(visitor);
  }
}
