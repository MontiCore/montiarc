package montiarc.helper;

import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeCompatibilityCheckerTest extends AbstractCoCoTest {

  @Test
  @Ignore
  public void hasNestedGenerics() {
    ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.ports", "PortCompatibilityWithGenerics");

    final Optional<PortSymbol> listListBoolIn = componentSymbol.getPort("listListBoolIn");
    assertTrue(listListBoolIn.isPresent());

    final boolean result = TypeCompatibilityChecker.hasNestedGenerics(listListBoolIn.get().getTypeReference());
    assertFalse("Port should have no nested generics", result);
  }

  @Test
  @Ignore
  public void doTypesMatch() {

    JTypeReference<? extends JTypeSymbol> sourceType = null;
    List<JTypeSymbol> sourceTypeFormalParams = null;
    List<JTypeReference<? extends JTypeSymbol>> sourceTypeTypeArguments = null;
    JTypeReference<? extends JTypeSymbol> targetType = null;
    List<JTypeSymbol> targetTypeFormalParams = null;
    List<JTypeReference<? extends JTypeSymbol>> targetTypeActualArguments = null;
    boolean result = false;

    result = TypeCompatibilityChecker.doTypesMatch(
        sourceType,
        sourceTypeFormalParams,
        sourceTypeTypeArguments,
        targetType,
        targetTypeFormalParams,
        targetTypeActualArguments);
    assertTrue("Matching types are not recognized as such.", result);
  }

  @Test
  public void areTypesEqualPrimitives() {
    final String packageName = "types.typecompatibility";
    final ComponentSymbol componentSymbol
        = loadComponentSymbol(packageName, "PrimitiveTypes");
    final Optional<PortSymbol> inBoolean = componentSymbol.getPort("inBoolean");
    assertTrue(inBoolean.isPresent());
    final Optional<PortSymbol> inInt = componentSymbol.getPort("inInt");
    assertTrue(inInt.isPresent());
    final PortSymbol inString = componentSymbol.getPort("inString").get();
    final Optional<PortSymbol> inChar = componentSymbol.getPort("inChar");
    assertTrue(inChar.isPresent());
    final Optional<PortSymbol> inLong = componentSymbol.getPort("inLong");
    assertTrue(inLong.isPresent());
    final Optional<PortSymbol> inByte = componentSymbol.getPort("inByte");
    assertTrue(inByte.isPresent());
    final Optional<PortSymbol> inDouble = componentSymbol.getPort("inDouble");
    assertTrue(inDouble.isPresent());


    // #################### START OF TESTS ####################

    // Check Integer, Integer
    boolean result = TypeCompatibilityChecker.areTypesEqual(
        inInt.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inInt.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertTrue(result);

    // Check Integer, Long
    result = TypeCompatibilityChecker.areTypesEqual(
        inInt.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inLong.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertFalse(result);

    // Check String, String
    result = TypeCompatibilityChecker.areTypesEqual(
        inString.getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inString.getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertTrue(result);


  }

  @Test
  public void areTypesEqualBoxed() {
    final String packageName = "types.typecompatibility";
    final ComponentSymbol componentSymbol
        = loadComponentSymbol(packageName, "BoxedTypes");

    final Optional<PortSymbol> inBooleanBoxed = componentSymbol.getPort("inBooleanBoxed");
    assertTrue(inBooleanBoxed.isPresent());
    final Optional<PortSymbol> inIntegerBoxed = componentSymbol.getPort("inIntegerBoxed");
    assertTrue(inIntegerBoxed.isPresent());
    final Optional<PortSymbol> inCharacterBoxed = componentSymbol.getPort("inCharacterBoxed");
    assertTrue(inCharacterBoxed.isPresent());
    final Optional<PortSymbol> inLongBoxed = componentSymbol.getPort("inLongBoxed");
    assertTrue(inLongBoxed.isPresent());
    final Optional<PortSymbol> inByteBoxed = componentSymbol.getPort("inByteBoxed");
    assertTrue(inByteBoxed.isPresent());
    final Optional<PortSymbol> inDoubleBoxed = componentSymbol.getPort("inDoubleBoxed");
    assertTrue(inDoubleBoxed.isPresent());

    final ComponentSymbol primitiveTypes
        = loadComponentSymbol(packageName, "PrimitiveTypes");
    final Optional<PortSymbol> inBoolean = primitiveTypes.getPort("inBoolean");
    assertTrue(inBoolean.isPresent());
    final Optional<PortSymbol> inInt = primitiveTypes.getPort("inInt");
    assertTrue(inInt.isPresent());
    final Optional<PortSymbol> inString = primitiveTypes.getPort("inString");
    assertTrue(inString.isPresent());
    final Optional<PortSymbol> inChar = primitiveTypes.getPort("inChar");
    assertTrue(inChar.isPresent());
    final Optional<PortSymbol> inLong = primitiveTypes.getPort("inLong");
    assertTrue(inLong.isPresent());
    final Optional<PortSymbol> inByte = primitiveTypes.getPort("inByte");
    assertTrue(inByte.isPresent());
    final Optional<PortSymbol> inDouble = primitiveTypes.getPort("inDouble");
    assertTrue(inDouble.isPresent());

    // #################### START OF TESTS ####################

    // Check int, Integer
    boolean result = TypeCompatibilityChecker.areTypesEqual(
        inInt.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inIntegerBoxed.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertTrue(result);

    // Check Integer, int
    result = TypeCompatibilityChecker.areTypesEqual(
        inIntegerBoxed.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inInt.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertTrue(result);

    // Check String, Integer
    result = TypeCompatibilityChecker.areTypesEqual(
        inString.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>(),
        inIntegerBoxed.get().getTypeReference(),
        new ArrayList<>(),
        new ArrayList<>());
    assertFalse(result);
  }

  @Test
  public void areTypesEqualWithGenerics() {
    final String packageName = "types.typecompatibility";
    final ComponentSymbol componentSymbol
        = loadComponentSymbol(packageName, "GenericTypes");

    // Load all required types from the models
    final Optional<ComponentInstanceSymbol> innerTInstance
        = componentSymbol.getSubComponent("innerTInstance");
    assertTrue(innerTInstance.isPresent());
    final Optional<ComponentSymbol> innerComponent = componentSymbol.getInnerComponent("Inner");
    assertTrue(innerComponent.isPresent());

    final List<JTypeSymbol> innerTypeParameters
        = innerComponent.get().getFormalTypeParameters();
    final List<ActualTypeArgument> innerTTypeArguments
        = innerTInstance.get().getComponentType().getActualTypeArguments();

    final Optional<ComponentInstanceSymbol> innerStringInstance
        = componentSymbol.getSubComponent("innerStringInstance");
    assertTrue(innerStringInstance.isPresent());
    final List<ActualTypeArgument> innerStringActualTypeArguments
        = innerStringInstance.get().getComponentType().getActualTypeArguments();

    final Optional<PortSymbol> portListString
        = componentSymbol.getPort("portListString");
    assertTrue(portListString.isPresent());
    final Optional<PortSymbol> portListInteger
        = componentSymbol.getPort("portListInteger");
    assertTrue(portListInteger.isPresent());
    final Optional<PortSymbol> portArrayListString
        = componentSymbol.getPort("portArrayListString");
    assertTrue(portArrayListString.isPresent());
    final Optional<PortSymbol> portListObject
        = componentSymbol.getPort("portListObject");
    assertTrue(portListObject.isPresent());
    final Optional<PortSymbol> portMapStringInteger
        = componentSymbol.getPort("portMapStringInteger");
    assertTrue(portMapStringInteger.isPresent());
    final Optional<PortSymbol> portHashMapStringInteger
        = componentSymbol.getPort("portHashMapStringInteger");
    assertTrue(portHashMapStringInteger.isPresent());
    final Optional<PortSymbol> portListT
        = componentSymbol.getPort("portListT");
    assertTrue(portListT.isPresent());
    final Optional<PortSymbol> portT = componentSymbol.getPort("portT");
    assertTrue(portT.isPresent());

    final Optional<PortSymbol> portListK
        = innerTInstance.get().getComponentType().getPort("portListK");
    assertTrue(portListK.isPresent());
    final Optional<PortSymbol> portK = innerComponent.get().getPort("portK");
    assertTrue(portK.isPresent());

    assertTrue(portListInteger.get().getTypeReference().existsReferencedSymbol());
    final List<? extends JTypeSymbol> listTypeParameters
        = portListInteger.get().getTypeReference().getReferencedSymbol().getFormalTypeParameters();


    // #################### START OF TESTS ####################

    // Check List<Integer>, List<Integer>
    boolean result = TypeCompatibilityChecker.areTypesEqual(
        portListInteger.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListInteger.get().getTypeReference().getActualTypeArguments()),
        portListInteger.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListInteger.get().getTypeReference().getActualTypeArguments()
        ));
    assertTrue(result);

    // Check List<String>, List<Integer>
    result = TypeCompatibilityChecker.areTypesEqual(
        portListString.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListString.get().getTypeReference().getActualTypeArguments()),
        portListInteger.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListInteger.get().getTypeReference().getActualTypeArguments()
        ));
    assertFalse(result);

    // Check ArrayList<String>, List<String>
    result = TypeCompatibilityChecker.areTypesEqual(
        portArrayListString.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portArrayListString.get().getTypeReference().getActualTypeArguments()),
        portListString.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListString.get().getTypeReference().getActualTypeArguments()
        ));
    assertFalse(result);

    // Check List<String>, ArrayList<String>
    result = TypeCompatibilityChecker.areTypesEqual(
        portListString.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListString.get().getTypeReference().getActualTypeArguments()),
        portArrayListString.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portArrayListString.get().getTypeReference().getActualTypeArguments()
        ));
    assertFalse(result);

    // Check T, String
    result = TypeCompatibilityChecker.areTypesEqual(
        portT.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            portListString.get().getTypeReference().getActualTypeArguments()),
        portK.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(innerComponent.get().getFormalTypeParameters()),
        TypeCompatibilityChecker.toJTypeReferences(
            innerStringActualTypeArguments
        ));
    assertFalse(result);

    // Check List<T>, List<K> from inner component with K -> T
    result = TypeCompatibilityChecker.areTypesEqual(
        portListT.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        new ArrayList<>(),
        portListK.get().getTypeReference(),
        TypeCompatibilityChecker.toJTypeSymbols(listTypeParameters),
        TypeCompatibilityChecker.toJTypeReferences(
            innerTInstance.get().getComponentType().getActualTypeArguments()
        ));
    assertTrue(result);

  }

  @Test
  public void test() {
    final String packageName = "components.body.subcomponents";
    final ComponentSymbol componentSymbol
        = loadComponentSymbol(packageName, "ComponentWithTypeParametersHasInstance");

    final Optional<PortSymbol> pIn = componentSymbol.getPort("pIn");
    assertTrue(pIn.isPresent());

    final Optional<ComponentSymbol> innerComponent = componentSymbol.getInnerComponent("Inner");
    assertTrue(innerComponent.isPresent());
    final Optional<PortSymbol> tIn = innerComponent.get().getPort("tIn");
    assertTrue(tIn.isPresent());

    final Optional<ComponentInstanceSymbol> inner = componentSymbol.getSubComponent("inner");
    assertTrue(inner.isPresent());


    // #################### START OF TESTS ####################

    // Check pIn -> inner.tIn
    boolean result = TypeCompatibilityChecker.areTypesEqual(
        pIn.get().getTypeReference(),
        componentSymbol.getFormalTypeParameters(),
        TypeCompatibilityChecker.toJTypeReferences(
            pIn.get().getTypeReference().getActualTypeArguments()),
        tIn.get().getTypeReference(),
        innerComponent.get().getFormalTypeParameters(),
        TypeCompatibilityChecker.toJTypeReferences(
            inner.get().getComponentType().getActualTypeArguments()
        ));
    assertTrue(result);
  }
}