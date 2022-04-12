/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.statements.ExpressionStatements
import testing.statements.IfStatements
import testing.statements.Loops
import testing.statements.NumberSwitcher

class StatementGeneratorTest {

  @Test
  fun `test whether if statement was generated correctly`() {
    runSimulation(IfStatements("generated")) {
      expectedForIf.keys.forEach {
        input("number").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", expectedForIf[it], output("t").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test whether switch case was generated correctly`() {
    runSimulation(NumberSwitcher("generated")) {
      expectedForSwitch.keys.forEach {
        input("number").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", expectedForSwitch[it], output("t").receive().payload)
        tickAtOutputs()
      }
    }
  }


  @Test
  fun `test whether loops were generated correctly`() {
    runSimulation(Loops("generated")) {
      expectedForLoops.keys.forEach {
        input("number").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", expectedForLoops[it], output("t").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test whether math expression statements are generated correctly`() {
    runSimulation(ExpressionStatements("generated")) {
      (-3..15).forEach {
        input("a").send(Message(it))
        tickInputs()
        Assert.assertEquals("b(a=$it)", b(it), output("b").receive().payload)
        Assert.assertEquals("d(a=$it)", d(it), output("d").receive().payload)
        tickAtOutputs()
      }
    }
  }

  private fun b(a: Int): Int = (a - 14) % 10 + ("\$O0$a").indexOf("1") * 2
  private fun d(a: Int): Double = (6) + a * 4 - (a + 1.7) / 12

  // (those are maps which means the iteration order is undefined)
  private val expectedForIf: Map<Int, String> = mapOf(
      2 to "then5",
      5 to "then5",
      18 to "else21",
      19 to "then19",
      27 to "else27",
      28 to "else31"
  )

  private val expectedForSwitch: Map<Int, String> = mapOf(
      100 to "usualFirstCase",
      200 to "doubleLabel",
      300 to "doubleLabel",
      400 to "noBreakBefore800",
      500 to "noBreakBefore500",
      666 to "usualMiddleCase",
      10 to "innerSwitch",
      20 to "innerDouble",
      30 to "innerDouble",
      1 to "innerInnerSwitch",
      41 to "innerInnerSwitch",
      2 to "subSubFrom402",
      42 to "subSubFrom442",
      3 to "defaultFromSubSubAnd50",
      44 to "subSubFrom44",
      49 to "defaultFromSubSubAnd50",
      50 to "defaultFromSubSubAnd50",
      60 to "innerDefault",
      700 to "caseAfterInnerSwitch",
      800 to "ifInCase",
      801 to "elseFromBefore",
      900 to "elseFromBefore",
      1000 to "otherIfInCase",
      1011 to "elseIf",
      1002 to "elseCase",
      1100 to "defaultFrom1101",
      1200 to "defaultFrom1201",
      1300 to "defaultFrom1300",
      8000 to "defaultFrom8000",
  )

  private val expectedForLoops: Map<Int, String> = mapOf(
      0 to "abcd",
      6 to "2345",
      1 to "",
      -6 to "024",
      -7 to "0246"
  )
}