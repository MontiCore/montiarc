/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.SourcePosition;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link TypeBound}.
 */
class RefinementTypeBoundTest extends ArcBasisTestBase {

  private static final String TEST_RESOURCE = "test/resources";
  private static final String PACKAGE = "cocos";

  @BeforeEach
  protected void initSymbols() {
    /*
     * Loading the following types:
     * Comparable<T>
     * String implements Comparable<String>
     * Integer implements Comparable<Integer>
     * component A
     * component B<T>
     * component C<T, U>
     * component D<T extends int>
     * component E<T extends Comparable<Integer>>
     * component F<T extends Comparable<String>, U extends Comparable<Integer>>
     * component G<T, U extends Comparable<T>>
     * component H<U extends Comparable<T>, T>
     */
    Path genericTypePath = Path.of(TEST_RESOURCE, PACKAGE, "GenericTypes.sym");
    Path genericCompPath = Path.of(TEST_RESOURCE, PACKAGE, "GenericComps.sym");
    ArcBasisSymbols2Json symbols2Json = new ArcBasisSymbols2Json();
    ArcBasisMill.globalScope().addSubScope(symbols2Json.load(genericTypePath.toString()));
    ArcBasisMill.globalScope().addSubScope(symbols2Json.load(genericCompPath.toString()));
  }

  @Test
  void shouldNotReportError1() {
    // Given: A
    ASTArcComponentType refiningComp = buildCompWithRefinement("A");
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError2() {
    // Given: B<int>
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", intExpr);
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError3() {
    // Given: B<Integer>
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError4() {
    // Given: C<int, int> 
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTArcComponentType refiningComp = buildCompWithRefinement("C", intExpr.deepClone(), intExpr.deepClone());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError5() {
    // Given: C<Integer, String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("C", integerExpr(), stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError6() {
    // Given: D<int> 
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTArcComponentType refiningComp = buildCompWithRefinement("D", intExpr);
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError7() {
    // Given: E<Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError8() {
    // Given: F<String, Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("F", stringExpr(), integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError9() {
    // Given: TypeVar T; reference B<T>
    TypeVarSymbol tTypeVar = typeVar("T");
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", typeVarExpr(tTypeVar));
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError10() {
    // Given: TypeVar T; reference C<T, T>
    TypeVarSymbol tTypeVar = typeVar("T");
    ASTArcComponentType refiningComp = buildCompWithRefinement("C", typeVarExpr(tTypeVar), typeVarExpr(tTypeVar));
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError11() {
    // Given: TypeVars T, U; reference C<T, U>
    TypeVarSymbol tTypeVar = typeVar("T");
    TypeVarSymbol uTypeVar = typeVar("U");
    ASTArcComponentType refiningComp = buildCompWithRefinement("C", typeVarExpr(tTypeVar), typeVarExpr(uTypeVar));
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError12() {
    // Given: TypeVar T extends Integer; reference E<T>
    TypeVarSymbol tTypeVar = typeVar("T", integerExpr());
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", typeVarExpr(tTypeVar));
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError13() {
    // Given: B (raw)
    ASTArcComponentType refiningComp = buildCompWithRefinement("B");
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError14() {
    // Given: C (raw)
    ASTArcComponentType refiningComp = buildCompWithRefinement("C");
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldReportError1() {
    // Given: D<boolean> 
    SymTypeExpression boolExpr = SymTypeExpressionFactory.createPrimitive("boolean");
    ASTArcComponentType refiningComp = buildCompWithRefinement("D", boolExpr);
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError2() {
    // Given: E<String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError3() {
    // Given: F<Integer, String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("F", integerExpr(), stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError4() {
    // Given: G<Integer, String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("G", integerExpr(), stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError5() {
    // Given: H<Integer, String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("H", integerExpr(), stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError6() {
    // Given: TypeVar T extends String; reference E<T> 
    TypeVarSymbol tTypeVar = typeVar("T", stringExpr());
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", typeVarExpr(tTypeVar));
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError7() {
    // Given: C<Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("C", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError8() {
    // Given: F<Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("F", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND,
      ArcError.TOO_FEW_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError9() {
    // Given: F<String> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("F", stringExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError10() {
    // Given: A<Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("A", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError11() {
    // Given: B<Integer, Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", integerExpr(), integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError12() {
    // Given: E<String, Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", stringExpr(), integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS,
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  @Test
  void shouldReportError13() {
    // Given: E<Integer, Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", integerExpr(), integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError14() {
    // Given: A<Integer>
    ASTArcComponentType refiningComp = buildCompWithRefinement("A", integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS
    ));
  }

  @Test
  void shouldReportError15() {
    // Given: E<String, Integer> 
    ASTArcComponentType refiningComp = buildCompWithRefinement("E", stringExpr(), integerExpr());
    TypeBound coco = new TypeBound();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_TYPE_ARGUMENTS,
      ArcError.TYPE_ARG_IGNORES_UPPER_BOUND
    ));
  }

  /** Creates a type variable symbol with the given name */
  protected static TypeVarSymbol typeVar(String name) {
    return ArcBasisMill.typeVarSymbolBuilder().setName(name).build();
  }

  /** Creates a type variable symbol with the given name and bounds */
  protected static TypeVarSymbol typeVar(String name, SymTypeExpression bound) {
    return ArcBasisMill.typeVarSymbolBuilder()
      .setName(name)
      .addSuperTypes(bound)
      .build();
  }

  /** Shortcut for {@link SymTypeExpressionFactory#createTypeVariable(TypeVarSymbol)} */
  protected static SymTypeExpression typeVarExpr(TypeVarSymbol typeVarSymbol) {
    return SymTypeExpressionFactory.createTypeVariable(typeVarSymbol);
  }

  /** Creates a {@link SymTypeExpression} that represents {@link Integer}. Integer's type symbol must already exist. */
  protected static SymTypeExpression integerExpr() {
    return SymTypeExpressionFactory.createTypeObject(
      ArcBasisMill.globalScope().resolveType("Integer").orElseThrow()
    );
  }

  /** Creates a {@link SymTypeExpression} that represents {@link String} . String's type symbol must already exist. */
  protected static SymTypeExpression stringExpr() {
    return SymTypeExpressionFactory.createTypeObject(
      ArcBasisMill.globalScope().resolveType("String").orElseThrow()
    );
  }

  /**
   * Creates a {@link ASTArcComponentType} with a symbol that refines the given abstraction with the given type
   * arguments
   *
   * @param abstractionName Raw name of the component type to refine (must be resolvable from the global scope)
   * @param typeArgs        Type arguments to use in the refinement
   */
  protected static ASTArcComponentType buildCompWithRefinement(@NotNull String abstractionName,
                                                               @NotNull SymTypeExpression... typeArgs) {
    Preconditions.checkNotNull(abstractionName);
    Preconditions.checkNotNull(typeArgs);

    ComponentTypeSymbol abstractionSym = ArcBasisMill.globalScope().resolveComponentType(abstractionName).orElseThrow();

    ASTMCObjectType sourceNode = ArcBasisMill.mCQualifiedTypeBuilder()
      .set_SourcePositionStart(SourcePosition.getDefaultSourcePosition())
      .set_SourcePositionEnd(SourcePosition.getDefaultSourcePosition())
      .uncheckedBuild();

    CompKindExpression compExpr = typeArgs.length == 0 ?
      new CompKindOfComponentType(abstractionSym) :
      new CompKindOfGenericComponentType(abstractionSym, Arrays.asList(typeArgs));
    compExpr.setSourceNode(sourceNode);

    ComponentTypeSymbol concretizationSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Dummy")
      .setSpannedScope(ArcBasisMill.scope())
      .addRefinements(compExpr)
      .build();

    ASTArcComponentType astConcretization = ArcBasisMill.arcComponentTypeBuilder()
      .setName("Dummy")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addSpec(ArcBasisMill.arcParentBuilder()
          .set_SourcePositionStart(SourcePosition.getDefaultSourcePosition())
          .set_SourcePositionEnd(SourcePosition.getDefaultSourcePosition())
          .uncheckedBuild())
        .build())
      .build();
    astConcretization.setSymbol(concretizationSym);

    return astConcretization;
  }
}
