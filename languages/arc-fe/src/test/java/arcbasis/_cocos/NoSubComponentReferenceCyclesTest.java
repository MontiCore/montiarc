/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
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
   * Provides a component symbol {@link #DUMMY_WITHOUT_CYCLE_NAME}. And adds it to the global scope if it was not in
   * there before.
   */
  protected static ComponentTypeSymbol provideDummyWithoutCycle() {
    if(ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITHOUT_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITHOUT_CYCLE_NAME).get();
    } else {
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
  protected static ComponentTypeSymbol provideDummyWithCycle() {
    if(ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_CYCLE_NAME).get();
    } else {
      ComponentTypeSymbol dummyWithCycle = provideDummyWithCycle_unlinked();
      ComponentTypeSymbol modelWithCycle = provideModelWithCycle_unlinked();

      dummyWithCycle.getSubComponent("sub").get().setType(new TypeExprOfComponent(modelWithCycle));
      modelWithCycle.getSubComponent("dummy").get().setType(new TypeExprOfComponent(dummyWithCycle));
      return dummyWithCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_CYCLE_NAME} with inner instance "sub". The type of that component is
   * not set yet, but should be of type {@link #MODEL_WITH_CYCLE_NAME}. If the built component symbol was not in the
   * global scope before, it is added to it.
   */
  protected static ComponentTypeSymbol provideDummyWithCycle_unlinked() {
    if(ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_CYCLE_NAME).get();
    } else {
      ComponentTypeSymbol dummyWithCycle = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(DUMMY_WITH_CYCLE_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
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
  protected static ComponentTypeSymbol provideDummyWithNestedCycle() {
    if(ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ComponentTypeSymbol dummyWithNestedCycle = provideDummyWithNestedCycle_unlinked();
      ComponentTypeSymbol modelWithNestedCycle = provideModelWithNestedCycle_unlinked();

      dummyWithNestedCycle.getInnerComponent("Sub1").get()
        .getInnerComponent("Sub2").get()
        .getInnerComponent("Sub3").get()
        .getSubComponent("withCyc").get().setType(new TypeExprOfComponent(modelWithNestedCycle));
      modelWithNestedCycle.getInnerComponent("Sub1").get()
        .getInnerComponent("Sub2").get()
        .getInnerComponent("Sub3").get()
        .getSubComponent("withCyc").get().setType(new TypeExprOfComponent(dummyWithNestedCycle));

      return dummyWithNestedCycle;
    }
  }

  /**
   * Builds a component symbol {@link #DUMMY_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type is
   * not set yet. You should set this type to the component type {@link #MODEL_WITH_NESTED_CYCLE_NAME} after this
   * method. The built symbol is added to the global scope, if it wasn't contained in there already.
   */
  protected static ComponentTypeSymbol provideDummyWithNestedCycle_unlinked() {
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

    if(ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(DUMMY_WITH_NESTED_CYCLE_NAME).get();
    } else {
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
        .build();
      subType3.getSpannedScope().add(innerCompInst);

      ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("sub1")
        .setType(new TypeExprOfComponent(subType1))
        .build();
      dummyWithNestedCycle.getSpannedScope().add(subInst1);

      ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("sub2")
        .setType(new TypeExprOfComponent(subType2))
        .build();
      subType1.getSpannedScope().add(subInst2);

      ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
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
  protected static ComponentTypeSymbol provideModelLongCycle1() {
    if (ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_1_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_1_NAME).get();
    } else {
      ComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubComponent("lc2").get().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubComponent("lc3").get().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubComponent("lc1").get().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp1;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_1_NAME} that has a component instance "lc2" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_2_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelLongCycle1_unlinked() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_1_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_1_NAME).get();
    } else {
      ComponentTypeSymbol longCycleType1 = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_1_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("lc2")
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

      ArcBasisMill.globalScope().add(longCycleType1);
      ArcBasisMill.globalScope().addSubScope(longCycleType1.getSpannedScope());

      return longCycleType1;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_2_NAME} that has a component instance of type
   * {@link #MODEL_LONG_CYCLE_3_NAME}. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelLongCycle2() {
    if (ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_2_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_2_NAME).get();
    } else {
      ComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubComponent("lc2").get().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubComponent("lc3").get().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubComponent("lc1").get().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp2;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_2_NAME} that has a component instance "lc3" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_3_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelLongCycle2_unlinked() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_2_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_2_NAME).get();
    } else {
      ComponentTypeSymbol longCycleType2 = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_2_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("lc3")
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

      ArcBasisMill.globalScope().add(longCycleType2);
      ArcBasisMill.globalScope().addSubScope(longCycleType2.getSpannedScope());

      return longCycleType2;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_3_NAME} that has a component instance of type
   * {@link #MODEL_LONG_CYCLE_1_NAME}. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelLongCycle3() {
    if (ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_3_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_3_NAME).get();
    } else {
      ComponentTypeSymbol longCycleComp1 = provideModelLongCycle1_unlinked();
      ComponentTypeSymbol longCycleComp2 = provideModelLongCycle2_unlinked();
      ComponentTypeSymbol longCycleComp3 = provideModelLongCycle3_unlinked();

      longCycleComp1.getSubComponent("lc2").get().setType(new TypeExprOfComponent(longCycleComp2));
      longCycleComp2.getSubComponent("lc3").get().setType(new TypeExprOfComponent(longCycleComp3));
      longCycleComp3.getSubComponent("lc1").get().setType(new TypeExprOfComponent(longCycleComp1));

      return longCycleComp3;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_LONG_CYCLE_3_NAME} that has a component instance "lc1" for whom no type
   * has been set yet. This type should be set to component type {@link #MODEL_LONG_CYCLE_1_NAME} after this method
   * call. If the built symbol does not yet exist in the global scope, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelLongCycle3_unlinked() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_3_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_LONG_CYCLE_3_NAME).get();
    } else {
      ComponentTypeSymbol longCycleType3 = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(MODEL_LONG_CYCLE_3_NAME)
        .setSpannedScope(ArcBasisMill.scope())
        .build();

      ComponentInstanceSymbol innerCompInst = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("lc1")
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
      .setType(new TypeExprOfComponent(fooComp))
      .build();
    modelWithoutCycle.getSpannedScope().add(fooInst);

    ComponentInstanceSymbol refToDummy = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("du1")
      .setType(new TypeExprOfComponent(provideDummyWithoutCycle()))
      .build();
    modelWithoutCycle.getSpannedScope().add(refToDummy);

    ComponentInstanceSymbol nestedRefToDummy = ArcBasisMill.componentInstanceSymbolBuilder()
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
  protected static ComponentTypeSymbol provideModelWithDirectSelfReference() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_DIRECT_SELF_REF_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_DIRECT_SELF_REF_NAME).get();
    } else {
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
  protected static ComponentTypeSymbol provideModelWithCycle() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_CYCLE_NAME).get();
    } else {
      ComponentTypeSymbol modelWithCycle = provideModelWithCycle_unlinked();
      ComponentTypeSymbol dummyWithCycle = provideDummyWithCycle_unlinked();

      modelWithCycle.getSubComponent("dummy").get().setType(new TypeExprOfComponent(dummyWithCycle));
      dummyWithCycle.getSubComponent("sub").get().setType(new TypeExprOfComponent(modelWithCycle));
      return modelWithCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_CYCLE_NAME} that has an inner instance named "dummy" whose type has
   * not yet been set. It should be set to {@link #DUMMY_WITH_CYCLE_NAME} after this method call. If the built component
   * symbol did not exist in the global scope before, it is added to it.
   */
  protected static ComponentTypeSymbol provideModelWithCycle_unlinked() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_CYCLE_NAME).get();
    } else {
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

      ComponentInstanceSymbol dummyInst = ArcBasisMill.componentInstanceSymbolBuilder()
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
  protected static ComponentTypeSymbol provideModelWithNestedCycle() {
    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_NESTED_CYCLE_NAME).get();
    } else {
      ComponentTypeSymbol modelWithNestedCycle = provideModelWithNestedCycle_unlinked();
      ComponentTypeSymbol dummyWithNestedCycle = provideDummyWithNestedCycle_unlinked();

      modelWithNestedCycle.getInnerComponent("Sub1").get()
        .getInnerComponent("Sub2").get()
        .getInnerComponent("Sub3").get()
        .getSubComponent("withCyc").get().setType(new TypeExprOfComponent(dummyWithNestedCycle));
      dummyWithNestedCycle.getInnerComponent("Sub1").get()
        .getInnerComponent("Sub2").get()
        .getInnerComponent("Sub3").get()
        .getSubComponent("withCyc").get().setType(new TypeExprOfComponent(modelWithNestedCycle));

      return modelWithNestedCycle;
    }
  }

  /**
   * Provides a component symbol {@link #MODEL_WITH_NESTED_CYCLE_NAME} that has a nested instance "withCyc" whose type
   * has not been set yet. After the execution of this method you should set it to the component type
   * {@link #DUMMY_WITH_NESTED_CYCLE_NAME}. If the built component symbol was not in the global scope already, it is
   * added to it.
   */
  protected static ComponentTypeSymbol provideModelWithNestedCycle_unlinked() {
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

    if(ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_NESTED_CYCLE_NAME).isPresent()) {
      return ArcBasisMill.globalScope().resolveComponentType(MODEL_WITH_NESTED_CYCLE_NAME).get();
    } else {
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
        .build();
      subType3.getSpannedScope().add(refToDummy);

      ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("sub1")
        .setType(new TypeExprOfComponent(subType1))
        .build();
      modelWithNestedCycle.getSpannedScope().add(subInst1);

      ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName("sub2")
        .setType(new TypeExprOfComponent(subType2))
        .build();
      subType1.getSpannedScope().add(subInst2);

      ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
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
      .setType(new TypeExprOfComponent(subType1))
      .build();
    subType3.getSpannedScope().add(cycleRef);

    ComponentInstanceSymbol subInst1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub1")
      .setType(new TypeExprOfComponent(subType1))
      .build();
    modelWithNestedCycle.getSpannedScope().add(subInst1);

    ComponentInstanceSymbol subInst2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub2")
      .setType(new TypeExprOfComponent(subType2))
      .build();
    subType1.getSpannedScope().add(subInst2);

    ComponentInstanceSymbol subInst3 = ArcBasisMill.componentInstanceSymbolBuilder()
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
    ComponentTypeSymbol compSym = provideModelWithoutCycle();
    ComponentTypeSymbol dummyHelper = provideDummyWithoutCycle();

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
      Arguments.arguments(provideModelWithCycle(), new ComponentTypeSymbol[]{provideDummyWithCycle()},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithDirectSelfReference(), new ComponentTypeSymbol[]{},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelLongCycle1(),
        new ComponentTypeSymbol[]{provideModelLongCycle2(), provideModelLongCycle3()},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithNestedCycle(), new ComponentTypeSymbol[]{provideDummyWithNestedCycle()},
        new ArcError[]{ArcError.NO_SUBCOMPONENT_CYCLE}),
      Arguments.arguments(provideModelWithSelfNestedCycle(), new ComponentTypeSymbol[]{},
        new ArcError[0])

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
    ASTComponentType astComp = compToTest.getAstNode();

    // When
    NoSubComponentReferenceCycles coco = new NoSubComponentReferenceCycles();
    coco.check(astComp);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}
