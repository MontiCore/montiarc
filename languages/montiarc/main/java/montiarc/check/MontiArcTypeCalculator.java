/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.AbstractArcTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.ArcBasisWithinScopeBasicSymbolsResolver;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in MontiArc.
 */
public class MontiArcTypeCalculator extends AbstractArcTypeCalculator {

  public MontiArcTypeCalculator() {
    this(init(new TypeCalculator3(MontiArcMill.traverser(), new Type4Ast(), new InferenceContext4Ast())));
  }

  protected MontiArcTypeCalculator(@NotNull TypeCalculator3 t) {
    super(t);
  }

  protected static TypeCalculator3 init(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    initMCCommonLiteralsTypeVisitor(t);
    ArcBasisTypeCalculator.initExpressionBasisTypeVisitor(t);
    initBitExpressionsTypeVisitor(t);
    initCommonExpressionsTypeVisitor(t);
    initAssignmentExpressionsTypeVisitor(t);
    ArcBasisTypeCalculator.initMCBasicTypesTypeVisitor(t);
    initMCCollectionTypesTypeVisitor(t);
    initMCSimpleGenericTypesTypeVisitor(t);
    initSetExpressionsTypeVisitor(t);
    // initialize the global delegate
    new MapBasedTypeCheck3(t.getTypeTraverser(), t.getType4Ast(), t.getCtx4Ast())
        .setThisAsDelegate();
    return t;
  }

  public static void initMCCommonLiteralsTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    MCCommonLiteralsTraverser traverser = (MCCommonLiteralsTraverser) t.getTypeTraverser();
    MCCommonLiteralsTypeVisitor visitor = new MCCommonLiteralsTypeVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4MCCommonLiterals(visitor);
  }

  public static void initCommonExpressionsTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    CommonExpressionsTraverser traverser = (CommonExpressionsTraverser) t.getTypeTraverser();
    CommonExpressionsCTTIVisitor visitor = new CommonExpressionsCTTIVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    visitor.setWithinTypeBasicSymbolsResolver(new MAOOWithinTypeBasicSymbolsResolver());
    visitor.setWithinScopeResolver(new ArcBasisWithinScopeBasicSymbolsResolver());
    traverser.add4CommonExpressions(visitor);
    traverser.setCommonExpressionsHandler(visitor);
  }

  public static void initAssignmentExpressionsTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    AssignmentExpressionsTraverser traverser = (AssignmentExpressionsTraverser) t.getTypeTraverser();
    AssignmentExpressionsCTTIVisitor visitor = new AssignmentExpressionsCTTIVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4AssignmentExpressions(visitor);
    traverser.setAssignmentExpressionsHandler(visitor);
  }

  public static void initBitExpressionsTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    BitExpressionsTraverser traverser = (BitExpressionsTraverser) t.getTypeTraverser();
    BitExpressionsTypeVisitor visitor = new BitExpressionsTypeVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4BitExpressions(visitor);
  }

  public static void initMCCollectionTypesTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    MCCollectionTypesTraverser traverser = (MCCollectionTypesTraverser) t.getTypeTraverser();
    MCCollectionTypesTypeVisitor visitor = new MCCollectionTypesTypeVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4MCCollectionTypes(visitor);
  }

  public static void initMCSimpleGenericTypesTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    MCSimpleGenericTypesTraverser traverser = (MCSimpleGenericTypesTraverser) t.getTypeTraverser();
    MCSimpleGenericTypesTypeVisitor visitor = new MCSimpleGenericTypesTypeVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4MCSimpleGenericTypes(visitor);
  }

  public static void initSetExpressionsTypeVisitor(@NotNull TypeCalculator3 t) {
    Preconditions.checkNotNull(t);
    SetExpressionsTraverser traverser = (SetExpressionsTraverser) t.getTypeTraverser();
    SetExpressionsCTTIVisitor visitor = new SetExpressionsCTTIVisitor();
    visitor.setType4Ast(t.getType4Ast());
    visitor.setContext4Ast(t.getCtx4Ast());
    traverser.add4SetExpressions(visitor);
    traverser.setSetExpressionsHandler(visitor);
  }
}
