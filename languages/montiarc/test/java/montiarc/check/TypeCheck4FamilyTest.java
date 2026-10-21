/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.context.WithinComponentResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._visitor.MontiArcTraverser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc._cocos.ExpressionStatementIsValid4Family;
import variablearc._cocos.VarDeclarationInitializationHasCorrectType4Family;
import variablearc.check.TypeCheck4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.ArrayList;
import java.util.List;

public class TypeCheck4FamilyTest extends MontiArcTestBase {

  protected MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    TypeCheck4Family typeCheck = new TypeCheck4Family(withinComponentResolver,
      new PresenceResolver(withinComponentResolver));
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType4Family(typeCheck));
    checker.addCoCo(new ExpressionStatementIsValid4Family(typeCheck));
    return checker;
  }

  @Test
  public void shouldTypeNamesPerConfiguration() {
    ASTMACompilationUnit ast = compile("""
      component A {
        feature f;
        varif (f) {
          port in int i;
          compute { int x = i + 1; }
        } else {
          port in boolean i;
          compute { boolean b = !i; }
        }
      }""");

    checker().checkAll(ast);

    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings()::toString);
  }

  @Test
  public void shouldReportMismatchOfSomeConfiguration() {
    ASTMACompilationUnit ast = compile("""
      component B {
        feature f;
        varif (f) {
          port in int i;
        } else {
          port in boolean i;
        }
        compute { int x = i; }
      }""");

    checker().checkAll(ast);

    Assertions.assertEquals(List.of("0xA0921"), this.codes());
    Assertions.assertTrue(Log.getFindings().get(0).isError());
  }

  @Test
  public void shouldReportNamesMissingInSomeConfiguration() {
    ASTMACompilationUnit ast = compile("""
      component C {
        feature f;
        varif (f) {
          port in int i;
        }
        compute { int x = i; }
      }""");

    checker().checkAll(ast);

    Assertions.assertEquals(List.of("0xFD118"), this.codes());
  }

  @Test
  public void shouldPreferLocalVariablesOverPorts() {
    ASTMACompilationUnit ast = compile("""
      component D {
        port in boolean i;
        compute {
          int i = 1;
          int x = i;
        }
      }""");

    checker().checkAll(ast);

    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings()::toString);
  }

  @Test
  public void shouldReportEqualFindingsOfDifferentCombinationsOnce() {
    ASTMACompilationUnit ast = compile("""
      component E {
        feature f;
        varif (f) {
          port in int i;
        } else {
          port in long i;
        }
        compute { boolean b = i > 0 ? 1 : 2; }
      }""");

    checker().checkAll(ast);

    Assertions.assertEquals(List.of("0xA0921"), this.codes());
  }

  @Test
  public void shouldNotChangeDerivedTypes() {
    ASTMACompilationUnit ast = compile("""
      component F {
        feature f;
        varif (f) {
          port in int i;
        } else {
          port in long i;
        }
        compute { long x = i; }
      }""");
    ASTNameExpression name = this.name(ast, "i");
    String before = TypeCheck3.typeOf(name).printFullName();

    checker().checkAll(ast);

    Assertions.assertEquals(before, TypeCheck3.typeOf(name).printFullName());
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings()::toString);
  }

  @Test
  public void shouldWarnForCombinationsDependingOnUnknownValues() {
    ASTMACompilationUnit ast = compile("""
      component G(String s) {
        varif (s == "a") {
          port in int i;
        } else {
          port in boolean i;
        }
        compute { boolean b = i; }
      }""");

    checker().checkAll(ast);

    Assertions.assertEquals(List.of("0xA0921"), this.codes());
    Assertions.assertTrue(Log.getFindings().get(0).isWarning());
  }

  @Test
  public void shouldResolveInheritedMembersWhereNotRedeclared() {
    compile("package a; component P { int i = 1; }");
    ASTMACompilationUnit ast = compile("""
      component I extends a.P {
        feature f;
        varif (f) {
          boolean i = true;
        }
        compute { int x = f ? 0 : i; }
      }""");

    checker().checkAll(ast);

    Assertions.assertEquals(List.of("0xA0921"), this.codes());
  }

  protected List<String> codes() {
    return Log.getFindings().stream().map(Finding::getMsg).map(msg -> msg.substring(0, 7)).toList();
  }

  protected ASTNameExpression name(ASTMACompilationUnit ast, String name) {
    List<ASTNameExpression> found = new ArrayList<>();
    MontiArcTraverser traverser = MontiArcMill.traverser();
    traverser.add4ExpressionsBasis(new ExpressionsBasisVisitor2() {
      @Override
      public void visit(ASTNameExpression node) {
        if (node.getName().equals(name)) found.add(node);
      }
    });
    ast.accept(traverser);
    return found.get(0);
  }
}
