/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link NoSubComponentReferenceCycles}.
 */
public class NoSubComponentReferenceCyclesTest extends AbstractTest {

  protected final static String DUMMY_WITHOUT_CYCLE_NAME = "DummyWithoutCycle";
  protected final static String DUMMY_WITH_CYCLE_NAME = "DummyWithCycle";
  protected final static String DUMMY_WITH_NESTED_CYCLE_NAME = "DummyWithNestedCycle";
  protected final static String MODEL_WITHOUT_CYCLE_NAME = "WithoutCycle";
  protected final static String MODEL_WITH_CYCLE_NAME = "WithCycle";
  protected final static String MODEL_WITH_NESTED_CYCLE_NAME = "WithNestedCycle";
  protected final static String MODEL_WITH_SELF_NESTED_CYCLE_NAME = "WithSelfNestedCycle";
  protected final static String MODEL_WITH_DIRECT_SELF_REF_NAME = "WithDirectSelfReference";
  protected final static String MODEL_LONG_CYCLE_1_NAME = "LongCycle1";
  protected final static String MODEL_LONG_CYCLE_2_NAME = "LongCycle2";
  protected final static String MODEL_LONG_CYCLE_3_NAME = "LongCycle3";
  /**
   * Provides a component symbol {@link #DUMMY_WITHOUT_CYCLE_NAME}.
   */
  protected static ComponentTypeSymbol provideDummyWithoutCycle() {
    ComponentTypeSymbol dummyWithoutCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(DUMMY_WITHOUT_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentTypeSymbol innerComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Inner")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    dummyWithoutCycle.getSpannedScope().add(innerComp);
    dummyWithoutCycle.getSpannedScope().addSubScope(innerComp.getSpannedScope());

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("i1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Inner")
        .setEnclosingScope(dummyWithoutCycle.getSpannedScope())
        .build()
      )
      .build();
    dummyWithoutCycle.getSpannedScope().add(innerCompInst);

    return dummyWithoutCycle;
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_CYCLE_NAME} that contains a reference to a component type
   * {@link #MODEL_WITH_CYCLE_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideDummyWithCycle() {
    ComponentTypeSymbol dummyWithCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(DUMMY_WITH_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_WITH_CYCLE_NAME)
        .setEnclosingScope(dummyWithCycle.getSpannedScope())
        .build()
      )
      .build();
    dummyWithCycle.getSpannedScope().add(innerCompInst);

    return dummyWithCycle;
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_NESTED_CYCLE_NAME} that contains a reference to a component type
   * {@link #MODEL_WITH_NESTED_CYCLE_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideDummyWithNestedCycle() {
    /*
     * We build:
     * component DummyWithNestedCycle {
     *   component Sub1 s1 {
     *     component Sub2 s2 {
     *       component Sub3 s3 {
     *         WithNestedCycle withCyc;
     *       }
     *     }
     *   }
     * }
     */

    ComponentTypeSymbol dummyWithNestedCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(DUMMY_WITH_NESTED_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentTypeSymbol subType1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    dummyWithNestedCycle.getSpannedScope().add(subType1);
    dummyWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

    ComponentTypeSymbol subType2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub2")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType1.getSpannedScope().add(subType2);
    subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

    ComponentTypeSymbol subType3 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub3")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType2.getSpannedScope().add(subType3);
    subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("withCyc")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_WITH_NESTED_CYCLE_NAME)
        .setEnclosingScope(subType3.getSpannedScope())
        .build()
      )
      .build();
    subType3.getSpannedScope().add(innerCompInst);

    ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub1")
        .setEnclosingScope(dummyWithNestedCycle.getSpannedScope())
        .build()
      )
      .build();
    dummyWithNestedCycle.getSpannedScope().add(subInst1);

    ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub2")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub2")
        .setEnclosingScope(subType1.getSpannedScope())
        .build()
      )
      .build();
    subType1.getSpannedScope().add(subInst2);

    ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub3")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub3")
        .setEnclosingScope(subType2.getSpannedScope())
        .build()
      )
      .build();
    subType2.getSpannedScope().add(subInst3);

    return dummyWithNestedCycle;
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_1_NAME} that contains a reference to a component type
   * {@link #MODEL_LONG_CYCLE_2_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelLongCycle1() {
    ComponentTypeSymbol longCycleType1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_LONG_CYCLE_1_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("lc2")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_LONG_CYCLE_2_NAME)
        .setEnclosingScope(longCycleType1.getSpannedScope())
        .build()
      )
      .build();
    longCycleType1.getSpannedScope().add(innerCompInst);

    longCycleType1.setAstNode(
      ArcBasisMill.componentTypeBuilder()
      .setName(MODEL_LONG_CYCLE_1_NAME)
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build()
    );
    longCycleType1.getAstNode().setSymbol(longCycleType1);

    return longCycleType1;
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_2_NAME} that contains a reference to a component type
   * {@link #MODEL_LONG_CYCLE_3_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelLongCycle2() {
    ComponentTypeSymbol longCycleType2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_LONG_CYCLE_2_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("lc3")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_LONG_CYCLE_3_NAME)
        .setEnclosingScope(longCycleType2.getSpannedScope())
        .build()
      )
      .build();
    longCycleType2.getSpannedScope().add(innerCompInst);

    longCycleType2.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_LONG_CYCLE_2_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    longCycleType2.getAstNode().setSymbol(longCycleType2);

    return longCycleType2;
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_3_NAME} that contains a reference to a component type
   * {@link #MODEL_LONG_CYCLE_1_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelLongCycle3() {
    ComponentTypeSymbol longCycleType3 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_LONG_CYCLE_3_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("lc1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_LONG_CYCLE_1_NAME)
        .setEnclosingScope(longCycleType3.getSpannedScope())
        .build()
      )
      .build();
    longCycleType3.getSpannedScope().add(innerCompInst);

    longCycleType3.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_LONG_CYCLE_3_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    longCycleType3.getAstNode().setSymbol(longCycleType3);

    return longCycleType3;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITHOUT_CYCLE_NAME} that contains a reference to a component type7
   * {@link #DUMMY_WITHOUT_CYCLE_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelWithoutCycle() {
    /*
      we build:
      component WithoutCycle {
        Foo { DummyWithoutCycle innerDummy; }
        Foo foo1;
        DummyWithoutCycle du1;
      }
     */
    ComponentTypeSymbol modelWithoutCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_WITHOUT_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithoutCycle.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_WITHOUT_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithoutCycle.getAstNode().setSymbol(modelWithoutCycle);

    ComponentTypeSymbol fooComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Foo")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    modelWithoutCycle.getSpannedScope().add(fooComp);
    modelWithoutCycle.getSpannedScope().addSubScope(fooComp.getSpannedScope());

    ComponentInstanceSymbol fooInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("foo1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Foo")
        .setEnclosingScope(modelWithoutCycle.getSpannedScope())
        .build()
      )
      .build();
    modelWithoutCycle.getSpannedScope().add(fooInst);

    ComponentInstanceSymbol refToDummy = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("du1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(DUMMY_WITHOUT_CYCLE_NAME)
        .setEnclosingScope(modelWithoutCycle.getSpannedScope())
        .build()
      )
      .build();
    modelWithoutCycle.getSpannedScope().add(refToDummy);

    ComponentInstanceSymbol nestedRefToDummy = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("innerDummy")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(DUMMY_WITHOUT_CYCLE_NAME)
        .setEnclosingScope(fooComp.getSpannedScope())
        .build()
      )
      .build();
    fooComp.getSpannedScope().add(nestedRefToDummy);

    return modelWithoutCycle;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_DIRECT_SELF_REF_NAME}.
   */
  protected static ComponentTypeSymbol provideModelWithDirectSelfReference() {
    ComponentTypeSymbol modelWithSelfRef = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_WITH_DIRECT_SELF_REF_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithSelfRef.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_WITH_DIRECT_SELF_REF_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithSelfRef.getAstNode().setSymbol(modelWithSelfRef);

    ComponentInstanceSymbol selfRefInstance = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("selfRef")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(MODEL_WITH_DIRECT_SELF_REF_NAME)
        .setEnclosingScope(modelWithSelfRef.getSpannedScope())
        .build()
      )
      .build();
    modelWithSelfRef.getSpannedScope().add(selfRefInstance);

    return modelWithSelfRef;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_CYCLE_NAME} that contains a reference to a component type
   * {@link #DUMMY_WITH_CYCLE_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelWithCycle() {
    ComponentTypeSymbol modelWithCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_WITH_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithCycle.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_WITH_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithCycle.getAstNode().setSymbol(modelWithCycle);

    ComponentInstanceSymbol dummyRef = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("dummy")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(DUMMY_WITH_CYCLE_NAME)
        .setEnclosingScope(modelWithCycle.getSpannedScope())
        .build()
      )
      .build();
    modelWithCycle.getSpannedScope().add(dummyRef);

    return modelWithCycle;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_NESTED_CYCLE_NAME} that contains a reference to a component type
   * {@link #DUMMY_WITH_NESTED_CYCLE_NAME} that is not build by this method yet. You have to provide it yourself.
   */
  protected static ComponentTypeSymbol provideModelWithNestedCycle() {
    /*
     * We build:
     * component WithNestedCycle {
     *   component Sub1 s1 {
     *     component Sub2 s2 {
     *       component Sub3 s3 {
     *         DummyWithNestedCycle withCyc;
     *       }
     *     }
     *   }
     * }
     */

    ComponentTypeSymbol modelWithNestedCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_WITH_NESTED_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithNestedCycle.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_WITH_NESTED_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithNestedCycle.getAstNode().setSymbol(modelWithNestedCycle);

    ComponentTypeSymbol subType1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    modelWithNestedCycle.getSpannedScope().add(subType1);
    modelWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

    ComponentTypeSymbol subType2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub2")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType1.getSpannedScope().add(subType2);
    subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

    ComponentTypeSymbol subType3 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub3")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType2.getSpannedScope().add(subType3);
    subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

    ComponentInstanceSymbol refToDummy = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("withCyc")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName(DUMMY_WITH_NESTED_CYCLE_NAME)
        .setEnclosingScope(subType3.getSpannedScope())
        .build()
      )
      .build();
    subType3.getSpannedScope().add(refToDummy);

    ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub1")
        .setEnclosingScope(modelWithNestedCycle.getSpannedScope())
        .build()
      )
      .build();
    modelWithNestedCycle.getSpannedScope().add(subInst1);

    ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub2")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub2")
        .setEnclosingScope(subType1.getSpannedScope())
        .build()
      )
      .build();
    subType1.getSpannedScope().add(subInst2);

    ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub3")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub3")
        .setEnclosingScope(subType2.getSpannedScope())
        .build()
      )
      .build();
    subType2.getSpannedScope().add(subInst3);

    return modelWithNestedCycle;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_SELF_NESTED_CYCLE_NAME}.
   */
  protected static ComponentTypeSymbol provideModelWithSelfNestedCycle() {
    /*
     * We build:
     * component WithSelfNestedCycle {
     *   component Sub1 s1 {
     *     component Sub2 s2 {
     *       component Sub3 s3 {
     *         Sub1 withCyc;
     *       }
     *     }
     *   }
     * }
     */

    ComponentTypeSymbol modelWithNestedCycle = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(MODEL_WITH_SELF_NESTED_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithNestedCycle.setAstNode(
      ArcBasisMill.componentTypeBuilder()
        .setName(MODEL_WITH_SELF_NESTED_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithNestedCycle.getAstNode().setSymbol(modelWithNestedCycle);

    ComponentTypeSymbol subType1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    modelWithNestedCycle.getSpannedScope().add(subType1);
    modelWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

    ComponentTypeSymbol subType2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub2")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType1.getSpannedScope().add(subType2);
    subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

    ComponentTypeSymbol subType3 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Sub3")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType2.getSpannedScope().add(subType3);
    subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

    ComponentInstanceSymbol cycleRef = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("withCyc")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub1")
        .setEnclosingScope(subType3.getSpannedScope())
        .build()
      )
      .build();
    subType3.getSpannedScope().add(cycleRef);

    ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub1")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub1")
        .setEnclosingScope(modelWithNestedCycle.getSpannedScope())
        .build()
      )
      .build();
    modelWithNestedCycle.getSpannedScope().add(subInst1);

    ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub2")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub2")
        .setEnclosingScope(subType1.getSpannedScope())
        .build()
      )
      .build();
    subType1.getSpannedScope().add(subInst2);

    ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub3")
      .setType(ArcBasisMill.componentTypeSymbolSurrogateBuilder()
        .setName("Sub3")
        .setEnclosingScope(subType2.getSpannedScope())
        .build()
      )
      .build();
    subType2.getSpannedScope().add(subInst3);

    return modelWithNestedCycle;
  }

  @Test
  public void shouldNotFindCycle() {
    // Given
    ComponentTypeSymbol compSym = provideModelWithoutCycle();
    ComponentTypeSymbol dummyHelper = provideDummyWithoutCycle();
    ArcBasisMill.globalScope().add(compSym);
    ArcBasisMill.globalScope().add(dummyHelper);
    ArcBasisMill.globalScope().addSubScope(compSym.getSpannedScope());
    ArcBasisMill.globalScope().addSubScope(dummyHelper.getSpannedScope());

    Preconditions.checkState(compSym.isPresentAstNode());
    ASTComponentType comp = compSym.getAstNode();

    // When
    NoSubComponentReferenceCycles coco = new NoSubComponentReferenceCycles();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  protected static Stream<Arguments> invalidModelsAndErrorProvider() {
    return Stream.of(
      Arguments.arguments(provideModelWithCycle(), new ComponentTypeSymbol[] {provideDummyWithCycle()},
        new ArcError[] {ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithDirectSelfReference(), new ComponentTypeSymbol[] {},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelLongCycle1(),
        new ComponentTypeSymbol[] {provideModelLongCycle2(), provideModelLongCycle3()},
        new ArcError[] {ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithNestedCycle(), new ComponentTypeSymbol[] {provideDummyWithNestedCycle()},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithSelfNestedCycle(), new ComponentTypeSymbol[] {},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE})

    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelsAndErrorProvider")
  public void shouldFindCycle(
    @NotNull ComponentTypeSymbol compToTest,
    @NotNull ComponentTypeSymbol[] contextComponents,
    @NotNull ArcError... expectedErrors) {
    Preconditions.checkArgument(contextComponents != null);
    Preconditions.checkArgument(expectedErrors != null);
    Preconditions.checkArgument(compToTest != null);
    Preconditions.checkArgument(compToTest.isPresentAstNode());

    // Given
    ArcBasisMill.globalScope().add(compToTest);
    ArcBasisMill.globalScope().addSubScope(compToTest.getSpannedScope());
    Arrays.stream(contextComponents).forEach(ArcBasisMill.globalScope()::add);
    Arrays.stream(contextComponents)
      .map(ComponentTypeSymbol::getSpannedScope)
      .forEach(ArcBasisMill.globalScope()::addSubScope);

    ASTComponentType astComp = compToTest.getAstNode();

    // When
    NoSubComponentReferenceCycles coco = new NoSubComponentReferenceCycles();
    coco.check(astComp);

    // Then
    checkOnlyExpectedErrorsPresent(new ArcError[] {ArcError.NO_SUBCOMPONENT_CYCLE});
  }
}
