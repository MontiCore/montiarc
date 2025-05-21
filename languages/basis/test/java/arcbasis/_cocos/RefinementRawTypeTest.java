/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ArcComponentTypeSymbol;
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
 * Tests {@link RefinementRawType}.
 */
class RefinementRawTypeTest extends ArcBasisTestBase {

  private static final String TEST_RESOURCE = "test/resources";
  private static final String PACKAGE = "cocos";

  @BeforeEach
  protected void initSymbols() {
    /*
     * Loading generic components, especially:
     *
     * component A
     * component B<T>
     * And before that: Comparable<T>, String, Integer (so that the components are loaded correctly)
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
    RefinementRawType coco = new RefinementRawType();
    
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
    RefinementRawType coco = new RefinementRawType();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldNotReportError3() {
    // Given: B<Integer>
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", integerExpr());
    RefinementRawType coco = new RefinementRawType();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }


  @Test
  void shouldNotReportError4() {
    // Given: TypeVar T; reference B<T>
    TypeVarSymbol tTypeVar = typeVar("T");
    ASTArcComponentType refiningComp = buildCompWithRefinement("B", typeVarExpr(tTypeVar));
    RefinementRawType coco = new RefinementRawType();

    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldReportWarning() {
    // Given: B
    ASTArcComponentType refiningComp = buildCompWithRefinement("B");
    RefinementRawType coco = new RefinementRawType();
    
    // When
    coco.check(refiningComp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.RAW_USE_OF_PARAMETRIZED_TYPE
    ));
  }

  /** Creates a type variable symbol with the given name */
  protected static TypeVarSymbol typeVar(String name) {
    return ArcBasisMill.typeVarSymbolBuilder().setName(name).build();
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

  /**
   * Creates a {@link ASTArcComponentType} with a symbol that refines the given abstraction with the given type
   * arguments
   * @param abstractionName Raw name of the component type to refine (must be resolvable from the global scope)
   * @param typeArgs Type arguments to use in the refinement
   */
  protected static ASTArcComponentType buildCompWithRefinement(@NotNull String abstractionName,
                                                               @NotNull SymTypeExpression... typeArgs) {
    Preconditions.checkNotNull(abstractionName);
    Preconditions.checkNotNull(typeArgs);

    ArcComponentTypeSymbol abstractionSym = ArcBasisMill.globalScope().resolveArcComponentType(abstractionName).orElseThrow();
    
    CompKindExpression compExpr = typeArgs.length == 0 ?
      new TypeExprOfComponent(abstractionSym) :
      new TypeExprOfGenericComponent(abstractionSym, Arrays.asList(typeArgs));

    ArcComponentTypeSymbol concretizationSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
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
