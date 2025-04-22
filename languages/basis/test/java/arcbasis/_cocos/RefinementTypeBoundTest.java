/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ComponentTypeSymbol;

import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
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
 * Tests {@link RefinementTypeBound}.
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
    ASTComponentType refiningComp = buildCompWithRefinement("A");
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError2() {
    // Given: B<int>
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTComponentType refiningComp = buildCompWithRefinement("B", intExpr);
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError3() {
    // Given: B<Integer>
    ASTComponentType refiningComp = buildCompWithRefinement("B", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError4() {
    // Given: C<int, int> 
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTComponentType refiningComp = buildCompWithRefinement("C", intExpr.deepClone(), intExpr.deepClone());
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError5() {
    // Given: C<Integer, String> 
    ASTComponentType refiningComp = buildCompWithRefinement("C", integerExpr(), stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError6() {
    // Given: D<int> 
    SymTypeExpression intExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    ASTComponentType refiningComp = buildCompWithRefinement("D", intExpr);
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError7() {
    // Given: E<Integer> 
    ASTComponentType refiningComp = buildCompWithRefinement("E", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError8() {
    // Given: F<String, Integer> 
    ASTComponentType refiningComp = buildCompWithRefinement("F", stringExpr(), integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError9() {
    // Given: TypeVar T; reference B<T>
    TypeVarSymbol tTypeVar = typeVar("T");
    ASTComponentType refiningComp = buildCompWithRefinement("B", typeVarExpr(tTypeVar));
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError10() {
    // Given: TypeVar T; reference C<T, T>
    TypeVarSymbol tTypeVar = typeVar("T");
    ASTComponentType refiningComp = buildCompWithRefinement("C", typeVarExpr(tTypeVar), typeVarExpr(tTypeVar));
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("C", typeVarExpr(tTypeVar), typeVarExpr(uTypeVar));
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError12() {
    // Given: TypeVar T extends Integer; reference E<T>
    TypeVarSymbol tTypeVar = typeVar("T", integerExpr());
    ASTComponentType refiningComp = buildCompWithRefinement("E", typeVarExpr(tTypeVar));
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError13() {
    // Given: B (raw)
    ASTComponentType refiningComp = buildCompWithRefinement("B");
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError14() {
    // Given: C (raw)
    ASTComponentType refiningComp = buildCompWithRefinement("C");
    RefinementTypeBound coco = new RefinementTypeBound();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldReportError1() {
    // Given: D<boolean> 
    SymTypeExpression boolExpr = SymTypeExpressionFactory.createPrimitive("boolean");
    ASTComponentType refiningComp = buildCompWithRefinement("D", boolExpr);
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("E", stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("F", integerExpr(), stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("G", integerExpr(), stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("H", integerExpr(), stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("E", typeVarExpr(tTypeVar));
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("C", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("F", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("F", stringExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("A", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("B", integerExpr(), integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("E", stringExpr(), integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("E", integerExpr(), integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("A", integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();
    
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
    ASTComponentType refiningComp = buildCompWithRefinement("E", stringExpr(), integerExpr());
    RefinementTypeBound coco = new RefinementTypeBound();

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

  /** Creates a {@link SymTypeExpression} that represents {@link String} . String's type symbol must already exist.*/
  protected static SymTypeExpression stringExpr() {
    return SymTypeExpressionFactory.createTypeObject(
      ArcBasisMill.globalScope().resolveType("String").orElseThrow()
    );
  }

  /**
   * Creates a {@link ASTComponentType} with a symbol that refines the given abstraction with the given type
   * arguments
   * @param abstractionName Raw name of the component type to refine (must be resolvable from the global scope)
   * @param typeArgs Type arguments to use in the refinement
   */
  protected static ASTComponentType buildCompWithRefinement(@NotNull String abstractionName,
                                                            @NotNull SymTypeExpression... typeArgs) {
    Preconditions.checkNotNull(abstractionName);
    Preconditions.checkNotNull(typeArgs);

    ComponentTypeSymbol abstractionSym = ArcBasisMill.globalScope().resolveComponentType(abstractionName).orElseThrow();

    CompKindExpression compExpr = typeArgs.length == 0 ?
      new TypeExprOfComponent(abstractionSym) :
      new TypeExprOfGenericComponent(abstractionSym, Arrays.asList(typeArgs));

    ComponentTypeSymbol concretizationSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Dummy")
      .setSpannedScope(ArcBasisMill.scope())
      .addRefinements(compExpr)
      .build();

    ASTComponentType astConcretization = ArcBasisMill.componentTypeBuilder()
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
