/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP2Delegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP3Delegator;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import variablearc._cocos.CircularInheritance4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CircularInheritance4FamilyTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setupEnums();
    setupComponents();
  }

  protected void setupEnums() {
    OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    MontiArcMill.globalScope().add(onOffEnumType);
  }

  protected void setupComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { feature f1; constraint(!f1); }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg(             "package a.b; component Comp6 {" +
      "feature f1;" +
      "varif(f1){ component C extends B{}}" +
      "else{ component D extends D{}}" +
      "constraint(f1);" +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new CircularInheritance4Family());

    ASTMACompilationUnit mainAST = compile(model);
    checker.checkAll(mainAST);

    String[] test = getLoggedErrorCodes();

    // Then
    assertThat(Log.getErrorCount() == 0);
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { feature f1; }",
    "package a.b; component Comp2 {" +
      "feature f1;" +
      "varif(f1){ component C extends A{} }" +
      "}",
    "package a.b; component Comp3 {" +
      "feature f1,f2;" +
      "varif(f1){ component C extends A {} }" +
      "varif(f2){ component D extends A {} }" +
      "}",
    "package a.b; component Comp4 {" +
      "feature f1,f2;" +
      "component C extends A {}" +
      "varif(f1){ component D extends C {} }" +
      "varif(f2){ component E extends D,A{} }" +
      "constraint(!f2);" +
      "}",
    "package a.b; component Comp5 {" +
      "feature f1,f2;" +
      "a.b.B b1;" +
      "varif(f1){ component C extends C {} }" +
      "constraint(f1 == b1.f1);" +
      "}",
    "package a.b; component Comp6 {" +
      "feature f1;" +
      "varif(f1){ component C extends B{}}" +
      "else{ component D extends D{}}" +
      "constraint(f1);" +
      "}",
    "package a.b; component Comp7 {" +
      "feature f1;" +
      "varif(f1) { component D extends D{}}" +
      "else { component C extends B{}}" +
      "constraint(!f1);" +
      "}",
    "package a.b; component Comp8 {" +
      "feature f1,f2;" +
      "varif(f1){ component C extends D{}}" +
      "varif(f2){ component D extends C{}}" +
      "constraint((f1 && !f2) || (!f1 && f2));" +
      "}",
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new CircularInheritance4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new CircularInheritance4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(  "component Comp1 extends Comp1 { feature f1;}",
        ArcError.CIRCULAR_INHERITANCE),
      arg(  "package a.b; component Comp2 {" +
                "feature f1;" +
                "varif(f1){ component C extends C{} }" +
                "}",
        ArcError.CIRCULAR_INHERITANCE),
      arg("package a.b; component Comp3 {" +
        "feature f1,f2;" +
        "varif(f1) { component C extends B{}}" +
        "varif(f2) { component D extends D{}}" +
        "constraint(f1 || f2);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE
      ),
      arg("package a.b; component Comp4 {" +
        "feature f1,f2;" +
        "varif(f1){ component C extends D{} }" +
        "varif(f2){ component D extends C{} }" +
        "constraint(f1 && f2);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE,
        ArcError.CIRCULAR_INHERITANCE),
      arg("package a.b; component Comp5 {" +
        "feature f1,f2;" +
        "a.b.B b1;" +
        "varif(f1){ component C extends C {} }" +
        "constraint(f1 != b1.f1);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE),
      arg("package a.b; component Comp6 {" +
        "feature f1;" +
        "varif(f1){ component C extends B{}}" +
        "else{ component D extends D{}}" +
        "constraint(!f1);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE),
      arg("package a.b; component Comp7 {" +
        "feature f1;" +
        "varif(f1) { component D extends D{}}" +
        "else { component C extends B{}}" +
        "constraint(f1);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE),
      arg("package a.b; component Comp8 {" +
        "feature f1,f2;" +
        "varif(f1){ component C extends D{}}" +
        "varif(f2){ component D extends C{}}" +
        "constraint(f1 && f2);" +
        "}",
        ArcError.CIRCULAR_INHERITANCE,
        ArcError.CIRCULAR_INHERITANCE)
    );
  }

  /**
   * If a component directly extends itself, then the context-condition should
   * report an error.
   */
  @Test
  public void shouldFindDirectCircularInheritance() {
    // Given
    ASTArcComponentType ast = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();

    ArcBasisMill.scopesGenitorDelegator().createFromAST(ast);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(ast);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(ast);

    CircularInheritance4Family coco = new CircularInheritance4Family();

    // When
    coco.check(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(
      getErrorCodes(ArcError.CIRCULAR_INHERITANCE)
    );
  }

  /**
   * If a component transitively extends itself, then the context-condition
   * should report an error.
   */
  @Test
  public void shouldFindTransitiveCircularInheritance() {
    // Given
    ASTArcComponentType a = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("B")
              .build())
            .build())
          .build())
        .build())
      .build();
    ASTArcComponentType b = ArcBasisMill.arcComponentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(a);
    stg.createFromAST(b);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(a);
    stc.createFromAST(b);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(a);
    st3.createFromAST(b);

    CircularInheritance4Family coco = new CircularInheritance4Family();

    // When
    coco.check(a);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(
      getErrorCodes(ArcError.CIRCULAR_INHERITANCE)
    );
  }

  /**
   * If a component transitively  and directly extends itself, then the context-condition
   * should report two errors.
   */
  @Test
  public void shouldFindMultipleInheritedCircularInheritance() {
    // Given
    ASTArcComponentType a = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("B")
              .build())
            .build())
          .build())
        .build())
      .build();
    ASTArcComponentType b = ArcBasisMill.arcComponentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(a);
    stg.createFromAST(b);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(a);
    stc.createFromAST(b);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(a);
    st3.createFromAST(b);

    CircularInheritance4Family coco = new CircularInheritance4Family();

    // When
    coco.check(a);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(
          ArcError.CIRCULAR_INHERITANCE,
          ArcError.CIRCULAR_INHERITANCE
        )
      );
  }

  /**
   * If a component does not circularly extend itself, then the
   * context-condition should not report an error, even if one of the
   * component's parents extends itself.
   */
  @Test
  public void shouldNotReportCircularInheritance() {
    // Given
    ASTArcComponentType parent = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();
    ASTArcComponentType child = ArcBasisMill.arcComponentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(parent);
    st3.createFromAST(child);

    CircularInheritance4Family coco = new CircularInheritance4Family();

    // When
    coco.check(parent);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(ArcError.CIRCULAR_INHERITANCE)
      );
  }

  /**
   * If there is no circular inheritance, then the context-condition should
   * not report an error.
   */
  @Test
  public void shouldNotFindCircularInheritance() {
    // Given
    ASTArcComponentType parent = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ASTArcComponentType child = ArcBasisMill.arcComponentTypeBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcBasisMill.componentHeadBuilder()
        .addArcParent(ArcBasisMill.arcParentBuilder()
          .setType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .build())
        .build())
      .build();

    ArcBasisScopesGenitorDelegator stg = ArcBasisMill.scopesGenitorDelegator();
    stg.createFromAST(parent);
    stg.createFromAST(child);
    ArcBasisScopesGenitorP2Delegator stc = ArcBasisMill.scopesGenitorP2Delegator();
    stc.createFromAST(parent);
    stc.createFromAST(child);
    ArcBasisScopesGenitorP3Delegator st3 = ArcBasisMill.scopesGenitorP3Delegator();
    st3.createFromAST(parent);
    st3.createFromAST(child);

    CircularInheritance4Family coco = new CircularInheritance4Family();

    // When
    coco.check(parent);
    coco.check(child);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }
}
