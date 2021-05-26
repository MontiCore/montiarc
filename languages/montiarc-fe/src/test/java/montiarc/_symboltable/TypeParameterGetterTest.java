/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Supplier;

public class TypeParameterGetterTest extends AbstractTest {

  private static final Path PATH = Paths.get(RELATIVE_MODEL_PATH, "montiarc", "_symboltable", "generics");

  @BeforeEach
  public void setGlobalScope(){
    MontiArcMill.globalScope().setModelPath(new ModelPath(PATH));
  }

  @Test
  public void areParametersProcessedCorrectly() throws IOException {
    MontiArcScopesGenitorDelegator symTab = new MontiArcScopesGenitorDelegator();
    MontiArcParser parser = MontiArcMill.parser();
    ASTMACompilationUnit asthma = parser.parse(PATH.resolve("TriGenericInstantiation.arc").toString()).orElseThrow(couldNot("parse ..Instantiation.arc"));
    symTab.createFromAST(parser.parse(PATH.resolve("TriGenericComponent.arc").toString()).orElseThrow(couldNot("parse ..Component.arc")));
    ComponentInstanceSymbol comp =
        symTab
            .createFromAST(asthma)
            .resolveComponentType("TriGenericInstantiation")
            .orElseThrow(couldNot("find component type"))
            .getSpannedScope()
            .resolveComponentInstance("comp")
            .orElseThrow(couldNot("find instance"));
    Map<TypeVarSymbol, SymTypeExpression> params = comp.getTypeParameterMapping();
    new MockTypeParameter("T", "Map<String,Integer>").checkMatch(params);
    new MockTypeParameter("U", "Double").checkMatch(params);
    new MockTypeParameter("V", "String").checkMatch(params);
  }

  private static class MockTypeParameter{
    final String typeString;
    final String valueString ;

    private MockTypeParameter(String type, String value) {
      this.typeString = type;
      this.valueString = value;
    }

    /**
     * makes sure, that there is exactly one parameter in the list, that has this type
     * and that the instanced value of that type matches either
     * @param mapping all parameters
     */
    private void checkMatch(Map<TypeVarSymbol, SymTypeExpression> mapping){
      int[] count = {0};
      mapping.forEach((type, value) -> {
        if(typeString.equals(type.getName())){
          count[0]++;
          Assertions.assertEquals(valueString, print(value), "The generic type " + typeString + " is not set as expected");
        }
      });
      Assertions.assertEquals(1, count[0], "Found no match for type " + typeString);
    }

    /**
     * @param value extension that has to be compared
     * @return the expression as string. Also removes fully qualified stuff the method appends to the string and whitespaces
     */
    private static String print(SymTypeExpression value){
      return value.print().replaceAll("(\\w[\\w\\d]*\\.)+|\\s","");
    }
  }

  private Supplier<RuntimeException> couldNot(String what){
    return () -> new RuntimeException("Could not "+what);
  }
}
