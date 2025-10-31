/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionStatementIsValid4FamilyTest extends MontiArcTestBase {
  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B {port in boolean i;}");
    compile("package a.b; component C { port out boolean o; }");
    compile("package a.b; component D { port in int i; }");
    compile("package a.b; component E { port out int o; }");
    compile("package a.b; component F { port in java.lang.Integer i; }");
    compile("package a.b; component G { port in java.util.List<java.lang.Integer> i; }");
    compile("package a.b; component H { port out java.util.List<java.lang.Integer> o; }");
    compile("package a.b; component I { port in java.lang.Comparable<java.lang.Integer> i; }");
    compile("package a.b; component J { port out java.lang.Comparable<java.lang.Integer> o; }");
    compile("package a.b; component K<T> { port in T i; } ");
    compile("package a.b; component L<T> { port out T o; }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg("package a.b; component Z { feature f1, f2,f3,f4;" +
      "port sync out int i;" +
      "varif(f1){double currentPosition = 0; }" +
      "varif(f2){int currentPosition = 0; }" +
      "varif(f3){boolean currentPosition = false; }"+
      "varif(f4){" +
      "init{i = currentPosition;}" +
      "automaton {\n" +
        "    initial state S;\n" +
        "    S -> S / {\n" +
        "\n" +
        "      i = currentPosition;\n" +
        "    }\n" +
        "  }}"+
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ExpressionStatementIsValid4Family());

    ASTMACompilationUnit mainAST = compile(model);
    checker.checkAll(mainAST);

    String[] test = getLoggedErrorCodes();

    // Then
    assertThat(Log.getErrorCount() == 0);
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }


}
