/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import arcbasis.check.deser.ArcBasisCompTypeExprDeSer;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.microsoft.z3.Context;
import variablearc._symboltable.VariableArcSymbolTableCompleter;
import variablearc._symboltable.VariableArcSymbolTableCompleterDelegator;
import variablearc.evaluation.VariableArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcSymbolTableCompleter;

  protected static VariableArcMill millVariableArcSymbolTableCompleterDelegator;

  protected static VariableArcMill millFullPrettyPrinter;

  protected static VariableArcMill millFullConverter;

  protected static VariableArcMill millCompTypeExprDeSer;

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

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millFullConverter == null) {
      millFullConverter = getMill();
    }
    return millFullConverter._fullConverter(context);
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcSymbolTableCompleter = a;
    millVariableArcSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
    millFullConverter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcSymbolTableCompleter = null;
    millVariableArcSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
    millFullConverter = null;
    millCompTypeExprDeSer = null;
  }

  protected VariableArcSymbolTableCompleter _symbolTableCompleter() {
    return new VariableArcSymbolTableCompleter();
  }

  protected VariableArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new VariableArcSymbolTableCompleterDelegator();
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new VariableArcDeriveSMTExpr(context);
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new ArcBasisCompTypeExprDeSer();  // TODO: replace with VarArc...DeSer when implemented
  }
}