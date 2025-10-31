/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP2Delegator;
import arcbasis._symboltable.ArcBasisScopesGenitorP3Delegator;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import variablearc._cocos.SubPortsConnected4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SubPortsConnected4FamilyTest extends MontiArcTestBase {

    @BeforeEach
    protected void initSymbols() {
        MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
        MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
        setupEnums();
        setupComponents();
    }

    protected void setupEnums()
    {
        OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        MontiArcMill.globalScope().add(onOffEnumType);
    }

    protected void setupComponents()
    {
      compile("package a.b; component A { }");
      compile("package a.b; component B { port in int i; }");
      compile("package a.b; component C { port out int o; }");
      compile("package a.b; component D { port in int i; port out int o; }");
      compile("package a.b; component E { port in int i1, i2; }");
      compile("package a.b; component F { port out int o1, o2; }");
    }

    private static Stream<Arguments> providePortsConnectedModel()
    {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel = arg("component Comp3 {" +
          "feature f1,f2; " +
          "varif(f1){port in int i; }" +
          "a.b.B b; " +
          "varif(f2){i -> b.i;" +
          "port in int i; }" +
          "constraint(f1 && f2);" +
          "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }


    @ParameterizedTest
    @MethodSource("providePortsConnectedModel")
    public void TestModelRuntimeMontiArcCoCos4Family(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new SubPortsConnected4Family());

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
            "component Comp1 { }",
            "component Comp2 { " +
                    "a.b.A a; " +
                    "}",
            "component Comp3 { " +
                    "port in int i; " +
                    "a.b.B b; " +
                    "i -> b.i; " +
                    "}",
            "component Comp4 { " +
                    "port out int o; " +
                    "a.b.C c; " +
                    "c.o -> o; " +
                    "}",
            "component Comp5 { " +
                    "port out int o1, o2; " +
                    "a.b.C c; " +
                    "c.o -> o1, o2; " +
                    "}",
            "component Comp6 { " +
                    "port in int i; " +
                    "port out int o; " +
                    "a.b.D d; " +
                    "i -> d.i; " +
                    "d.o -> o; " +
                    "}",
            "component Comp7 { " +
                    "port in int i; " +
                    "a.b.E e; " +
                    "i -> e.i1; " +
                    "i -> e.i2; " +
                    "}",
            "component Comp8 { " +
                    "port in int i; " +
                    "a.b.E e; " +
                    "i -> e.i1, e.i2; " +
                    "}",
            "component Comp9 { " +
                    "port out int o1, o2; " +
                    "a.b.F f; " +
                    "f.o1 -> o1; " +
                    "f.o2 -> o2; " +
                    "}",
            "component Comp10 { " +
                    "port in int i; " +
                    "port out int o; " +
                    "component Inner {" +
                    "port in int i; " +
                    "port out int o; " +
                    "}" +
                    "Inner sub; " +
                    "i -> sub.i; " +
                    "sub.o -> o; " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new SubPortsConnected4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { feature f1; }",
    "component Comp2 {" +
      "feature f1;" +
      "varif(f1){ a.b.B b; }" +
      "else{ " +
      "a.b.A a; }" +
      "constraint(!f1);" +
      "}",
    "component Comp3 {" +
      "feature f1,f2; " +
      "varif(f1){port in int i; }" +
      "a.b.B b; " +
      "varif(f2){i -> b.i; }" +
      "constraint(f1 && f2);" +
      "}",
    "component Comp4 { " +
      "feature f1;" +
      "varif(f1){ port out int o; " +
      "a.b.C c; " +
      "c.o -> o; }" +
      "}",
    "component Comp5 { " +
      "feature f1,f2;" +
      "varif(f1){ port out int o1, o2; }" +
      "varif(f2){ a.b.C c; c.o -> o1, o2; }" +
      "constraint(f1);" +
      "}",
    "component Comp6 { " +
      "feature f1,f2,f3,f4,f5;" +
      "varif(f1){ port in int i; }" +
      "varif(f2){ port out int o; }" +
      "varif(f3){ a.b.D d; }" +
      "varif(f4){ i -> d.i; }" +
      "varif(f5){ d.o -> o; }" +
      "constraint(f1 && f2 && f3 && f4 && f5);"+
      "}",
    "component Comp7 {" +
      "feature f1; " +
      "port in int i; " +
      "varif(f1){a.b.E e; " +
      "i -> e.i1; " +
      "i -> e.i2; }" +
      "}",
    "component Comp8 { " +
      "feature f1,f2;" +
      "port in int i; " +
      "varif(f1){ a.b.E e; }" +
      "varif(f2){ a.b.A a; }else {" +
      "i -> e.i1, e.i2; }" +
      "constraint(f1 && !f2);" +
      "}",
    "component Comp9 { " +
      "feature f1;" +
      "varif(f1){" +
      "port out int o1, o2; " +
      "a.b.F f; " +
      "f.o1 -> o1; " +
      "f.o2 -> o2; }" +
      "}",
    "component Comp10 {" +
      "feature f1,f2; " +
      "port in int i;" +
      "port out int o;" +
      "component Inner {" +
      "feature f1,f2;" +
      "varif(f1){ port in int i; }" +
      "varif(f2){ port out int o; }" +
      "constraint(f1 && f2);" +
      "}" +
      "Inner sub; " +
      "varif(f1){i -> sub.i;} " +
      "varif(f2){sub.o -> o;} " +
      "constraint(f1 == sub.f1 && f2 == sub.f2);" +
      "}"
  })

  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new SubPortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }


    @ParameterizedTest
    @MethodSource("invalidModels")
    public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
        Preconditions.checkNotNull(model);
        Preconditions.checkNotNull(errors);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new SubPortsConnected4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                arg("component Comp1 { " +
                                "a.b.B b; " +
                                "}",
                        ArcError.IN_PORT_NOT_CONNECTED),
                arg("component Comp2 { " +
                                "a.b.C c; " +
                                "}",
                        ArcError.OUT_PORT_NOT_CONNECTED),
                arg("component Comp3 { " +
                                "a.b.D d; " +
                                "}",
                        ArcError.IN_PORT_NOT_CONNECTED,
                        ArcError.OUT_PORT_NOT_CONNECTED),
                arg("component Comp4 { " +
                                "a.b.B b; " +
                                "a.b.C c; " +
                                "}",
                        ArcError.IN_PORT_NOT_CONNECTED,
                        ArcError.OUT_PORT_NOT_CONNECTED),
                arg("component Comp5 { " +
                                "a.b.E e; " +
                                "}",
                        ArcError.IN_PORT_NOT_CONNECTED,
                        ArcError.IN_PORT_NOT_CONNECTED),
                arg("component Comp6 { " +
                                "a.b.F f; " +
                                "}",
                        ArcError.OUT_PORT_NOT_CONNECTED,
                        ArcError.OUT_PORT_NOT_CONNECTED),
                arg("component Comp7 { " +
                                "a.b.B b1, b2; " +
                                "}",
                        ArcError.IN_PORT_NOT_CONNECTED,
                        ArcError.IN_PORT_NOT_CONNECTED),
                arg("component Comp8 { " +
                                "a.b.C c1, c2; " +
                                "}",
                        ArcError.OUT_PORT_NOT_CONNECTED,
                        ArcError.OUT_PORT_NOT_CONNECTED)
        );
    }

  HashMap<String, ASTArcComponentType> components;

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  void shouldDetectWronglyConnectedPorts(@NotNull String comp, @NotNull ArcError[] errors) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(errors);

    //Given
    ASTArcComponentType ast = components.get(comp);
    SubPortsConnected4Family coco = new SubPortsConnected4Family();

    //When
    coco.check(ast);

    //Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  @ParameterizedTest
  @MethodSource("invalidModelsWithVariability")
  public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new SubPortsConnected4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }


  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      arg("component Comp1 {" +
          "feature f1; " +
          "varif(f1){ a.b.B b; }" +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp2 {" +
          "feature f1;" +
          "varif(f1){ a.b.B b;}else{ " +
          "a.b.C c; }" +
          "constraint(!f1);" +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp3 {" +
          "feature f1; " +
          "varif(f1){a.b.D d; }" +
          "constraint(f1);" +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp4 {" +
          "feature f1,f2; " +
          "varif(f1){ a.b.B b; }" +
          "varif(f2){ a.b.C c; }" +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp5 { " +
          "feature f1;" +
          "varif(f1){a.b.E e; }" +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp6 {" +
          "feature f1,f2;" +
          "varif(f1){  " +
          "a.b.F f; }" +
          "varif(f2){" +
          "a.b.F f;}" +
          "constraint((f1 && !f2) || (!f1 && f2));" +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp7 {" +
          "feature f1; " +
          "varif(f1){a.b.B b1, b2; }" +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp8 {" +
          "feature f1; " +
          "varif(f1){ a.b.A a;}" +
          "else{a.b.C c1, c2;} " +
          "constraint(!f1);" +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED)
    );
  }


  @BeforeEach
  public void setUpTest() {
    ArcBasisScopesGenitorDelegator scopesGen = ArcBasisMill.scopesGenitorDelegator();
    ArcBasisScopesGenitorP2Delegator scopesGenP2 = ArcBasisMill.scopesGenitorP2Delegator();
    ArcBasisScopesGenitorP3Delegator scopesGenP3 = ArcBasisMill.scopesGenitorP3Delegator();

    ASTArcComponentType comp1 = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("i1", "i2")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("o1")
            .build())
          .build())
        .build())
      .build();
    scopesGen.createFromAST(comp1);
    ASTArcComponentType comp2 = ArcBasisMill.arcComponentTypeBuilder().setName("B")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true).
            setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("o1")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .setComponentInstanceList("sub1")
          .build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1",
          "sub1.i2").build())
        .build())
      .build();
    scopesGen.createFromAST(comp2);
    ASTArcComponentType comp3 = ArcBasisMill.arcComponentTypeBuilder().setName("C")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setIncoming(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("i1")
            .build())
          .addPortDeclaration(ArcBasisMill.portDeclarationBuilder()
            .setOutgoing(true)
            .setMCType(ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build())
            .setPortList("o1", "o2")
            .build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
              .addParts("A")
              .build())
            .build())
          .setComponentInstanceList("sub1")
          .build())
        .addArcElement(
          ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("sub1.i1").build())
        .addArcElement(
          ArcBasisMill.connectorBuilder().setSource("sub1.o1").setTargetList("o1").build())
        .build())
      .build();
    scopesGen.createFromAST(comp3);
    scopesGenP2.createFromAST(comp1);
    scopesGenP2.createFromAST(comp2);
    scopesGenP2.createFromAST(comp3);

    scopesGenP3.createFromAST(comp1);
    scopesGenP3.createFromAST(comp2);
    scopesGenP3.createFromAST(comp3);

    components = new HashMap<>();
    components.put(comp1.getName(), comp1);
    components.put(comp2.getName(), comp2);
    components.put(comp3.getName(), comp3);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ArcError[] errors1 = new ArcError[] {};
    ArcError[] errors2 = new ArcError[] { ArcError.OUT_PORT_NOT_CONNECTED};
    ArcError[] errors3 = new ArcError[] { ArcError.IN_PORT_NOT_CONNECTED};
    return Stream.of(Arguments.of("A", errors1), Arguments.of("B", errors2), Arguments.of("C", errors3));
  }
}
