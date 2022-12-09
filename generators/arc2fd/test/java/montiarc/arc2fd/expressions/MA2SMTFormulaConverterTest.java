/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MA2SMTFormulaConverterTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;

  MA2SMTFormulaConverter ma2smtConverter;

  // Store Variables which are used by multiple tests
  BooleanFormula a, b, c;
  BooleanFormula orFormula;
  BooleanFormula andFormula;
  BooleanFormula noCNFFormula;
  ASTExpression a_and_b;
  ASTExpression a_and_not_a;

  @BeforeEach
  public void setUp() throws InvalidConfigurationException {
    this.ma2smtConverter = new MA2SMTFormulaConverter();
    this.fmgr = ma2smtConverter.getFmgr();
    this.bmgr = ma2smtConverter.getBmgr();

    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.orFormula = bmgr.or(b, c);
    this.andFormula = bmgr.and(a, this.orFormula);
    this.noCNFFormula = bmgr.or(this.orFormula, this.andFormula);

    ASTExpression a_exp =
        MontiArcMill.nameExpressionBuilder().setName("a").build();
    ASTExpression b_exp =
        MontiArcMill.nameExpressionBuilder().setName("b").build();
    a_and_b =
        MontiArcMill.booleanAndOpExpressionBuilder().setLeft(a_exp).setRight(b_exp).setOperator("&&").build();
    a_and_not_a =
        MontiArcMill.booleanAndOpExpressionBuilder().setLeft(a_exp).setRight(
            MontiArcMill.logicalNotExpressionBuilder().setExpression(a_exp).build()).setOperator("&&").build();
  }

  /**
   * Method under test
   * {@link MA2SMTFormulaConverter#convert(ASTExpression, Map)}
   */
  @Test
  void convert() {
    // When
    Map<String, String> variableRemapping = new HashMap<>();
    BooleanFormula f1 = ma2smtConverter.convert(a_and_b, variableRemapping);

    variableRemapping.put("a", "z");
    BooleanFormula f2 = ma2smtConverter.convert(a_and_not_a, variableRemapping);

    // Then
    String f1Real = "(and a b)";
    String f2Real = "(and z (not z))";
    Assertions.assertEquals(f1Real, f1.toString());
    Assertions.assertEquals(f2Real, f2.toString());
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#isUnsat(BooleanFormula)}
   */
  @Test
  void isUnsat() throws SolverException, InterruptedException {
    // When && Then
    // orFormula => sat!
    Assertions.assertFalse(ma2smtConverter.isUnsat(this.orFormula));

    // andFormula && !andFormula => unsat
    Assertions.assertTrue(ma2smtConverter.isUnsat(bmgr.and(this.andFormula,
        bmgr.not(this.andFormula))));
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#buildFormula()}
   */
  @Test
  void buildFormula() {
    // Given
    ma2smtConverter.formulaStack = List.of(orFormula);

    // When
    BooleanFormula finalFormula = ma2smtConverter.buildFormula();

    // Then
    Assertions.assertEquals(finalFormula, ma2smtConverter.formula);
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#and()}
   */
  @Test
  void and() {
    // Given
    ma2smtConverter.addVariable("a");
    ma2smtConverter.addVariable("b");

    // When
    ma2smtConverter.and();

    // Then
    Assertions.assertEquals(List.of(bmgr.and(a, b)),
        ma2smtConverter.formulaStack);
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#or()}
   */
  @Test
  void or() {
    // Given
    ma2smtConverter.addVariable("a");
    ma2smtConverter.addVariable("b");

    // When
    ma2smtConverter.or();

    // Then
    Assertions.assertEquals(List.of(bmgr.or(a, b)),
        ma2smtConverter.formulaStack);
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#not()}
   */
  @Test
  void not() {
    // Given
    ma2smtConverter.addVariable("a");

    // When
    ma2smtConverter.not();

    // Then
    Assertions.assertEquals(List.of(bmgr.not(a)), ma2smtConverter.formulaStack);
  }

  /**
   * Method under test {@link MA2SMTFormulaConverter#ifThenElse()} ()}
   */
  @Test
  void ite() {
    // Given
    ma2smtConverter.addVariable("a");
    ma2smtConverter.addVariable("b");
    ma2smtConverter.addVariable("c");

    // When
    ma2smtConverter.ifThenElse();

    // Then
    Assertions.assertEquals(List.of(bmgr.ifThenElse(a, b, c)),
        ma2smtConverter.formulaStack);
  }
}
