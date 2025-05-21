/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds tests for the handwritten methods of {@link NoSubcomponentReferenceCycle}.
 */
public class NoSubComponentReferenceCyclesTest extends ArcBasisTestBase {

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
   * Provides a component symbol {@link #DUMMY_WITHOUT_CYCLE_NAME}. And adds it to the global scope if it was not in
   * there before.
   */
  protected static ArcComponentTypeSymbol provideDummyWithoutCycle() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITHOUT_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITHOUT_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol dummyWithoutCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(DUMMY_WITHOUT_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ArcComponentTypeSymbol innerComp = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Inner")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      dummyWithoutCycle.getSpannedScope().add(innerComp);
      dummyWithoutCycle.getSpannedScope().addSubScope(innerComp.getSpannedScope());

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("i1")
        .setType(new TypeExprOfComponent(innerComp))
        .build();
      dummyWithoutCycle.getSpannedScope().add(innerCompInst);

      ArcBasisMill.globalScope().add(dummyWithoutCycle);
      ArcBasisMill.globalScope().addSubScope(dummyWithoutCycle.getSpannedScope());

      return dummyWithoutCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_CYCLE_NAME} that instantiates a component of type {@link
   * #MODEL_WITH_CYCLE_NAME}. If the built component type was not in the global scope before, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideDummyWithCycle() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol dummyWithCycle = provideDummyWithCycle_unlinked();
      ArcComponentTypeSymbol modelWithCycle = provideModelWithCycle_unlinked();

      dummyWithCycle.getSubcomponents("sub").orElseThrow().setType(new TypeExprOfComponent(modelWithCycle));
      modelWithCycle.getSubcomponents("dummy").orElseThrow().setType(new TypeExprOfComponent(dummyWithCycle));
      return dummyWithCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_CYCLE_NAME} with inner instance "sub". The type of that component is
   * not set yet, but should be of type {@link #MODEL_WITH_CYCLE_NAME}. If the built component symbol was not in the
   * global scope before, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideDummyWithCycle_unlinked() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol dummyWithCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(DUMMY_WITH_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub")
        .build();
      dummyWithCycle.getSpannedScope().add(innerCompInst);

      ArcBasisMill.globalScope().add(dummyWithCycle);
      ArcBasisMill.globalScope().addSubScope(dummyWithCycle.getSpannedScope());

      return dummyWithCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type is
   * {@link #MODEL_WITH_NESTED_CYCLE_NAME}. The built symbol is added to the global scope, if it wasn't contained in
   * there already.
   */
  protected static ArcComponentTypeSymbol provideDummyWithNestedCycle() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol dummyWithNestedCycle = provideDummyWithNestedCycle_unlinked();
      ArcComponentTypeSymbol modelWithNestedCycle = provideModelWithNestedCycle_unlinked();

      dummyWithNestedCycle.getInnerComponent("Sub1").orElseThrow()
        .getInnerComponent("Sub2").orElseThrow()
        .getInnerComponent("Sub3").orElseThrow()
        .getSubcomponents("withCyc").orElseThrow().setType(new TypeExprOfComponent(modelWithNestedCycle));
      modelWithNestedCycle.getInnerComponent("Sub1").orElseThrow()
        .getInnerComponent("Sub2").orElseThrow()
        .getInnerComponent("Sub3").orElseThrow()
        .getSubcomponents("withCyc").orElseThrow().setType(new TypeExprOfComponent(dummyWithNestedCycle));

      return dummyWithNestedCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type is
   * not set yet. You should set this type to the component type {@link #MODEL_WITH_NESTED_CYCLE_NAME} after this
   * method. The built symbol is added to the global scope, if it wasn't contained in there already.
   */
  protected static ArcComponentTypeSymbol provideDummyWithNestedCycle_unlinked() {
    /*
     * We build:
     * component DummyWithNestedCycle {
     *   component Sub1 s1 {
     *     component Sub2 s2 {
     *       component Sub3 s3 {
     *         WithNestedCycle withCyc;   // <- the type of withCyc is not set yet.
     *       }
     *     }
     *   }
     * }
     */

    if(ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol dummyWithNestedCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(DUMMY_WITH_NESTED_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ArcComponentTypeSymbol subType1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub1")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      dummyWithNestedCycle.getSpannedScope().add(subType1);
      dummyWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

      ArcComponentTypeSymbol subType2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub2")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      subType1.getSpannedScope().add(subType2);
      subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

      ArcComponentTypeSymbol subType3 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub3")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      subType2.getSpannedScope().add(subType3);
      subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("withCyc")
        .build();
      subType3.getSpannedScope().add(innerCompInst);

      SubcomponentSymbol subInst1 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub1")
        .setType(new TypeExprOfComponent(subType1))
        .build();
      dummyWithNestedCycle.getSpannedScope().add(subInst1);

      SubcomponentSymbol subInst2 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub2")
        .setType(new TypeExprOfComponent(subType2))
        .build();
      subType1.getSpannedScope().add(subInst2);

      SubcomponentSymbol subInst3 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub3")
        .setType(new TypeExprOfComponent(subType3))
        .build();
      subType2.getSpannedScope().add(subInst3);

      ArcBasisMill.globalScope().add(dummyWithNestedCycle);
      ArcBasisMill.globalScope().addSubScope(dummyWithNestedCycle.getSpannedScope());

      return dummyWithNestedCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_1_NAME} that has a component instance of type
   * {@link #MODEL_LONG_CYCLE_2_NAME}. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle1() {
    if (ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_1_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_1_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ArcComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ArcComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubcomponents("lc2").orElseThrow().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubcomponents("lc3").orElseThrow().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubcomponents("lc1").orElseThrow().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp1;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_1_NAME} that has a component instance "lc2" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_2_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle1_unlinked() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_1_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_1_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleType1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_1_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("lc2")
        .build();
      longCycleType1.getSpannedScope().add(innerCompInst);

      longCycleType1.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_LONG_CYCLE_1_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      longCycleType1.getAstNode().setSymbol(longCycleType1);

      ArcBasisMill.globalScope().add(longCycleType1);
      ArcBasisMill.globalScope().addSubScope(longCycleType1.getSpannedScope());

      return longCycleType1;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_2_NAME} that has a component instance of type
   * {@link #MODEL_LONG_CYCLE_3_NAME}. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle2() {
    if (ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_2_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_2_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ArcComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ArcComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubcomponents("lc2").orElseThrow().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubcomponents("lc3").orElseThrow().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubcomponents("lc1").orElseThrow().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp2;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_2_NAME} that has a component instance "lc3" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_3_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle2_unlinked() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_2_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_2_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleType2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_2_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("lc3")
        .build();
      longCycleType2.getSpannedScope().add(innerCompInst);

      longCycleType2.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_LONG_CYCLE_2_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      longCycleType2.getAstNode().setSymbol(longCycleType2);

      ArcBasisMill.globalScope().add(longCycleType2);
      ArcBasisMill.globalScope().addSubScope(longCycleType2.getSpannedScope());

      return longCycleType2;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_3_NAME} that has a component instance of type
   * {@link #MODEL_LONG_CYCLE_1_NAME}. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle3() {
    if (ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_3_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_3_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ArcComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ArcComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubcomponents("lc2").orElseThrow().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubcomponents("lc3").orElseThrow().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubcomponents("lc1").orElseThrow().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp3;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_3_NAME} that has a component instance "lc1" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_1_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelLongCycle3_unlinked() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_3_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_LONG_CYCLE_3_NAME).get();
    } else {
      ArcComponentTypeSymbol longCycleType3 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_3_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      SubcomponentSymbol innerCompInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("lc1")
        .build();
      longCycleType3.getSpannedScope().add(innerCompInst);

      longCycleType3.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_LONG_CYCLE_3_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      longCycleType3.getAstNode().setSymbol(longCycleType3);

      ArcBasisMill.globalScope().add(longCycleType3);
      ArcBasisMill.globalScope().addSubScope(longCycleType3.getSpannedScope());

      return longCycleType3;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITHOUT_CYCLE_NAME} that has nested subcomponents of component type
   * {@link #DUMMY_WITHOUT_CYCLE_NAME}. If the created component symbol was not in the global scope before, it is added
   * to it.
   */
  protected static ArcComponentTypeSymbol provideModelWithoutCycle() {
    /*
      we build:
      component WithoutCycle {
        Foo { DummyWithoutCycle innerDummy; }
        Foo foo1;
        DummyWithoutCycle du1;
      }
     */
    ArcComponentTypeSymbol modelWithoutCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(MODEL_WITHOUT_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithoutCycle.setAstNode(
      ArcBasisMill.arcComponentTypeBuilder()
        .setName(MODEL_WITHOUT_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithoutCycle.getAstNode().setSymbol(modelWithoutCycle);

    ArcComponentTypeSymbol fooComp = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Foo")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    modelWithoutCycle.getSpannedScope().add(fooComp);
    modelWithoutCycle.getSpannedScope().addSubScope(fooComp.getSpannedScope());

    SubcomponentSymbol fooInst = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("foo1")
      .setType(new TypeExprOfComponent(fooComp))
      .build();
    modelWithoutCycle.getSpannedScope().add(fooInst);

    SubcomponentSymbol refToDummy = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("du1")
      .setType(new TypeExprOfComponent(provideDummyWithoutCycle()))
      .build();
    modelWithoutCycle.getSpannedScope().add(refToDummy);

    SubcomponentSymbol nestedRefToDummy = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("innerDummy")
      .setType(new TypeExprOfComponent(provideDummyWithoutCycle()))
      .build();
    fooComp.getSpannedScope().add(nestedRefToDummy);

    ArcBasisMill.globalScope().add(modelWithoutCycle);
    ArcBasisMill.globalScope().addSubScope(modelWithoutCycle.getSpannedScope());

    return modelWithoutCycle;
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_DIRECT_SELF_REF_NAME} and adds it to the global scope if the global
   * scope did not contain it before.
   */
  protected static ArcComponentTypeSymbol provideModelWithDirectSelfReference() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_DIRECT_SELF_REF_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_DIRECT_SELF_REF_NAME).get();
    } else {
      ArcComponentTypeSymbol modelWithSelfRef = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_WITH_DIRECT_SELF_REF_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      modelWithSelfRef.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_WITH_DIRECT_SELF_REF_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      modelWithSelfRef.getAstNode().setSymbol(modelWithSelfRef);

      SubcomponentSymbol selfRefInstance = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("selfRef")
        .setType(new TypeExprOfComponent(modelWithSelfRef))
        .build();
      modelWithSelfRef.getSpannedScope().add(selfRefInstance);

      ArcBasisMill.globalScope().add(modelWithSelfRef);
      ArcBasisMill.globalScope().addSubScope(modelWithSelfRef.getSpannedScope());

      return modelWithSelfRef;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_CYCLE_NAME} that instantiates a component of type {@link
   * #DUMMY_WITH_CYCLE_NAME}. If the built component symbol did not exist in the global scope before, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelWithCycle() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol modelWithCycle = provideModelWithCycle_unlinked();
      ArcComponentTypeSymbol dummyWithCycle = provideDummyWithCycle_unlinked();

      modelWithCycle.getSubcomponents("dummy").orElseThrow().setType(new TypeExprOfComponent(dummyWithCycle));
      dummyWithCycle.getSubcomponents("sub").orElseThrow().setType(new TypeExprOfComponent(modelWithCycle));
      return modelWithCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_CYCLE_NAME} that has an inner instance named "dummy" whose type has
   * not yet been set. It should be set to {@link #DUMMY_WITH_CYCLE_NAME} after this method call. If the built component
   * symbol did not exist in the global scope before, it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelWithCycle_unlinked() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol modelWithCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_WITH_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      modelWithCycle.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_WITH_CYCLE_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      modelWithCycle.getAstNode().setSymbol(modelWithCycle);

      SubcomponentSymbol dummyInst = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("dummy")
        .build();
      modelWithCycle.getSpannedScope().add(dummyInst);

      ArcBasisMill.globalScope().add(modelWithCycle);
      ArcBasisMill.globalScope().addSubScope(modelWithCycle.getSpannedScope());

      return modelWithCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type
   * has is {@link #DUMMY_WITH_NESTED_CYCLE_NAME}. If the built component symbol was not in the global scope already,
   * it is added to it.
   */
  protected static ArcComponentTypeSymbol provideModelWithNestedCycle() {
    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol modelWithNestedCycle = provideModelWithNestedCycle_unlinked();
      ArcComponentTypeSymbol dummyWithNestedCycle = provideDummyWithNestedCycle_unlinked();

      modelWithNestedCycle.getInnerComponent("Sub1").orElseThrow()
        .getInnerComponent("Sub2").orElseThrow()
        .getInnerComponent("Sub3").orElseThrow()
        .getSubcomponents("withCyc").orElseThrow().setType(new TypeExprOfComponent(dummyWithNestedCycle));
      dummyWithNestedCycle.getInnerComponent("Sub1").orElseThrow()
        .getInnerComponent("Sub2").orElseThrow()
        .getInnerComponent("Sub3").orElseThrow()
        .getSubcomponents("withCyc").orElseThrow().setType(new TypeExprOfComponent(modelWithNestedCycle));

      return modelWithNestedCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type
   * has not been set yet. After the execution of this method you should set it to the component type
   * {@link #DUMMY_WITH_NESTED_CYCLE_NAME}. If the built component symbol was not in the global scope already, it is
   * added to it.
   */
  protected static ArcComponentTypeSymbol provideModelWithNestedCycle_unlinked() {
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

    if(ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveArcComponentType(MODEL_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ArcComponentTypeSymbol modelWithNestedCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName(MODEL_WITH_NESTED_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      modelWithNestedCycle.setAstNode(
        ArcBasisMill.arcComponentTypeBuilder()
          .setName(MODEL_WITH_NESTED_CYCLE_NAME)
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build()
      );
      modelWithNestedCycle.getAstNode().setSymbol(modelWithNestedCycle);

      ArcComponentTypeSymbol subType1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub1")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      modelWithNestedCycle.getSpannedScope().add(subType1);
      modelWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

      ArcComponentTypeSymbol subType2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub2")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      subType1.getSpannedScope().add(subType2);
      subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

      ArcComponentTypeSymbol subType3 = ArcBasisMill.arcComponentTypeSymbolBuilder()
        .setName("Sub3")
        .setSpannedScope(ArcBasisMill.scope())
        .build();
      subType2.getSpannedScope().add(subType3);
      subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

      SubcomponentSymbol refToDummy = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("withCyc")
        .build();
      subType3.getSpannedScope().add(refToDummy);

      SubcomponentSymbol subInst1 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub1")
        .setType(new TypeExprOfComponent(subType1))
        .build();
      modelWithNestedCycle.getSpannedScope().add(subInst1);

      SubcomponentSymbol subInst2 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub2")
        .setType(new TypeExprOfComponent(subType2))
        .build();
      subType1.getSpannedScope().add(subInst2);

      SubcomponentSymbol subInst3 = ArcBasisMill.subcomponentSymbolBuilder()
        .setName("sub3")
        .setType(new TypeExprOfComponent(subType3))
        .build();
      subType2.getSpannedScope().add(subInst3);

      ArcBasisMill.globalScope().add(modelWithNestedCycle);
      ArcBasisMill.globalScope().addSubScope(modelWithNestedCycle.getSpannedScope());

      return modelWithNestedCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_SELF_NESTED_CYCLE_NAME}. Although this model is not valid, this
   * cycle should not be found by the coco. This is because usually every component type is checked by the coco, and
   * this may react at Sub1, Sub2 and Sub3. But the component WithSelfNestedCycle is independent of the circle, so
   * the coco should not fail on this component. Or else every component indirectly containing WithSelfNestedCycle would
   * also fail, which would result in endless error messages. The created component symbol is added to the global scope
   * in case it was not in there before.
   */
  protected static ArcComponentTypeSymbol provideModelWithSelfNestedCycle() {
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

    ArcComponentTypeSymbol modelWithNestedCycle = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(MODEL_WITH_SELF_NESTED_CYCLE_NAME)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    modelWithNestedCycle.setAstNode(
      ArcBasisMill.arcComponentTypeBuilder()
        .setName(MODEL_WITH_SELF_NESTED_CYCLE_NAME)
        .setHead(Mockito.mock(ASTComponentHead.class))
        .setBody(Mockito.mock(ASTComponentBody.class))
        .build()
    );
    modelWithNestedCycle.getAstNode().setSymbol(modelWithNestedCycle);

    ArcComponentTypeSymbol subType1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Sub1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    modelWithNestedCycle.getSpannedScope().add(subType1);
    modelWithNestedCycle.getSpannedScope().addSubScope(subType1.getSpannedScope());

    ArcComponentTypeSymbol subType2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Sub2")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType1.getSpannedScope().add(subType2);
    subType1.getSpannedScope().addSubScope(subType2.getSpannedScope());

    ArcComponentTypeSymbol subType3 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Sub3")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    subType2.getSpannedScope().add(subType3);
    subType2.getSpannedScope().addSubScope(subType3.getSpannedScope());

    SubcomponentSymbol cycleRef = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("withCyc")
      .setType(new TypeExprOfComponent(subType1))
      .build();
    subType3.getSpannedScope().add(cycleRef);

    SubcomponentSymbol subInst1 = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub1")
      .setType(new TypeExprOfComponent(subType1))
      .build();
    modelWithNestedCycle.getSpannedScope().add(subInst1);

    SubcomponentSymbol subInst2 = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub2")
      .setType(new TypeExprOfComponent(subType2))
      .build();
    subType1.getSpannedScope().add(subInst2);

    SubcomponentSymbol subInst3 = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub3")
      .setType(new TypeExprOfComponent(subType3))
      .build();
    subType2.getSpannedScope().add(subInst3);

    ArcBasisMill.globalScope().add(modelWithNestedCycle);
    ArcBasisMill.globalScope().addSubScope(modelWithNestedCycle.getSpannedScope());

    return modelWithNestedCycle;
  }

  @Test
  public void shouldNotFindCycle() {
    // Given
    ArcComponentTypeSymbol compSym = provideModelWithoutCycle();

    Preconditions.checkState(compSym.isPresentAstNode());
    ASTArcComponentType comp = compSym.getAstNode();

    // When
    NoSubcomponentReferenceCycle coco = new NoSubcomponentReferenceCycle();
    coco.check(comp);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  protected static Stream<Arguments> invalidModelsAndErrorProvider() {
    return Stream.of(
      Arguments.arguments(provideModelWithCycle(), new ArcComponentTypeSymbol[]{provideDummyWithCycle()},
        new ArcError[]{ArcError.SUBCOMPONENT_REFERENCE_CYCLE}),
      Arguments.arguments(provideModelWithDirectSelfReference(), new ArcComponentTypeSymbol[]{},
        new ArcError[]{ArcError.SUBCOMPONENT_REFERENCE_CYCLE}),
      Arguments.arguments(provideModelLongCycle1(),
        new ArcComponentTypeSymbol[]{provideModelLongCycle2(), provideModelLongCycle3()},
        new ArcError[]{ArcError.SUBCOMPONENT_REFERENCE_CYCLE}),
      Arguments.arguments(provideModelWithNestedCycle(), new ArcComponentTypeSymbol[]{provideDummyWithNestedCycle()},
        new ArcError[]{ArcError.SUBCOMPONENT_REFERENCE_CYCLE}),
      Arguments.arguments(provideModelWithSelfNestedCycle(), new ArcComponentTypeSymbol[]{},
        new ArcError[0])

    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelsAndErrorProvider")
  public void shouldFindCycle(
    @NotNull ArcComponentTypeSymbol compToTest,
    @NotNull ArcComponentTypeSymbol[] contextComponents,
    @NotNull ArcError... expectedErrors) {
    Preconditions.checkNotNull(contextComponents);
    Preconditions.checkNotNull(expectedErrors);
    Preconditions.checkNotNull(compToTest);
    Preconditions.checkArgument(compToTest.isPresentAstNode());

    // Given
    ASTArcComponentType astComp = compToTest.getAstNode();

    // When
    NoSubcomponentReferenceCycle coco = new NoSubcomponentReferenceCycle();
    coco.check(astComp);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }
}
