/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis.AbstractTest;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.MontiArcMill;
import montiarc.util.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExpressionToFDHelperTest extends AbstractTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;
  ExpressionToFDHelper<BooleanFormula> expressionToFDHelper;

  @BeforeEach
  public void setUp() throws InvalidConfigurationException {
    this.expressionToFDHelper = new ExpressionToFDHelper<>();
    this.fmgr = expressionToFDHelper.fmgr;
    this.bmgr = expressionToFDHelper.bmgr;
  }

  /**
   * Method under test
   * {@link ExpressionToFDHelper#convertASTExpressionToFD(ASTExpression, Formula, Map)}
   */
  @Test
  public void testAST2FDConversion() throws InterruptedException {
    // Given
    ASTExpression a_exp =
        MontiArcMill.nameExpressionBuilder().setName("a").build();
    ASTExpression b_exp =
        MontiArcMill.nameExpressionBuilder().setName("b").build();
    ASTExpression a_and_b =
        MontiArcMill.booleanAndOpExpressionBuilder().setLeft(a_exp).setRight(b_exp).setOperator("&&").build();
    ASTExpression a_and_not_a =
        MontiArcMill.booleanAndOpExpressionBuilder().setLeft(a_exp).setRight(
            MontiArcMill.logicalNotExpressionBuilder().setExpression(a_exp).build()).setOperator("&&").build();
    BooleanFormula a = bmgr.makeVariable("a");
    BooleanFormula b = bmgr.makeVariable("b");

    // CHECK a && b EXPRESSION (everything is ok)
    BooleanFormula root = bmgr.makeVariable("root");
    Map<String, String> variableRemapping = new HashMap<>();
    Optional<FDConstructionStorage<BooleanFormula>> storage;
    storage = expressionToFDHelper.convertASTExpressionToFD(a_and_b, root,
        variableRemapping);
    Assertions.assertTrue(storage.isPresent());

    // Check that all Entries are as expected
    Assertions.assertEquals(Set.of(a, b), storage.get().getAllRightSideAtoms());
    Assertions.assertEquals(Set.of(root), storage.get().getAllLeftSideAtoms());
    Assertions.assertEquals(0,
        storage.get().getExcludes().getRelationsHashMap().size());
    Assertions.assertEquals(0,
        storage.get().getOptionals().getRelationsHashMap().size());
    Assertions.assertEquals(0,
        storage.get().getRequires().getRelationsHashMap().size());
    Assertions.assertEquals(0,
        storage.get().getExcludes().getRelationsHashMap().size());

    // Check that the "Remaining Conjunctions" have the correct, expected
    // hash map
    HashMap<BooleanFormula, Set<BooleanFormula>> expectedHashMap =
        new HashMap<>();
    expectedHashMap.put(root, Set.of(a, b));
    Assertions.assertEquals(expectedHashMap,
        storage.get().getRemainingConjunctions().getRelationsHashMap());

    // CHECK a && !a EXPRESSION (=> "unsat" & empty storage!)
    storage = expressionToFDHelper.convertASTExpressionToFD(a_and_not_a, root
        , variableRemapping);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.FORMULA_IS_UNSAT});
    Assertions.assertTrue(storage.isEmpty());
  }

  /**
   * Method under test {@link ExpressionToFDHelper#isUnsat(BooleanFormula)} )}}}
   */
  @Test
  public void isUnsat() {
    // Given
    BooleanFormula a = bmgr.makeVariable("a");
    BooleanFormula b = bmgr.makeVariable("b");
    BooleanFormula c = bmgr.makeVariable("c");
    BooleanFormula orFormula = bmgr.or(b, c);
    BooleanFormula andFormula = bmgr.and(a, orFormula);
    BooleanFormula unsatFormula = bmgr.and(a, bmgr.not(a));

    // When
    Assertions.assertFalse(this.expressionToFDHelper.isUnsat(a));
    Assertions.assertFalse(this.expressionToFDHelper.isUnsat(b));
    Assertions.assertFalse(this.expressionToFDHelper.isUnsat(c));
    Assertions.assertFalse(this.expressionToFDHelper.isUnsat(orFormula));
    Assertions.assertFalse(this.expressionToFDHelper.isUnsat(andFormula));

    this.expressionToFDHelper.isUnsat(unsatFormula);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.FORMULA_IS_UNSAT});
  }
}
