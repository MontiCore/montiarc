/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import arcbasis._visitor.IFullPrettyPrinter;
import com.microsoft.z3.Context;
import de.monticore.prettyprint.IndentPrinter;
import variablearc._symboltable.VariableArcSymbolTableCompleter;
import variablearc._symboltable.VariableArcSymbolTableCompleterDelegator;
import variablearc._prettyprint.VariableArcFullPrettyPrinter;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;
import variablearc.evaluation.VariableArcDeriveSMTExpr;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcSymbolTableCompleter;

  protected static VariableArcMill millVariableArcSymbolTableCompleterDelegator;

  protected static VariableArcMill millFullPrettyPrinter;

  protected static VariableArcMill millFullConverter;

  public static VariableArcSymbolTableCompleter symbolTableCompleter() {
    if (millVariableArcSymbolTableCompleter == null) {
      millVariableArcSymbolTableCompleter = getMill();
    }
    return millVariableArcSymbolTableCompleter._symbolTableCompleter();
  }

  public static VariableArcSymbolTableCompleterDelegator symbolTableCompleterDelegator() {
    if (millVariableArcSymbolTableCompleterDelegator == null) {
      millVariableArcSymbolTableCompleterDelegator = getMill();
    }
    return millVariableArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  public static IFullPrettyPrinter fullPrettyPrinter() {
    if (millFullPrettyPrinter == null) {
      millFullPrettyPrinter = getMill();
    }
    return millFullPrettyPrinter._fullPrettyPrinter();
  }

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millFullConverter == null) {
      millFullConverter = getMill();
    }
    return millFullConverter._fullConverter(context);
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcSymbolTableCompleter = a;
    millVariableArcSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
    millFullConverter = a;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcSymbolTableCompleter = null;
    millVariableArcSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
    millFullConverter = null;
  }

  protected VariableArcSymbolTableCompleter _symbolTableCompleter() {
    return new VariableArcSymbolTableCompleter();
  }

  protected VariableArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new VariableArcSymbolTableCompleterDelegator();
  }

  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return new VariableArcFullPrettyPrinter(new IndentPrinter());
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new VariableArcDeriveSMTExpr(context);
  }
}