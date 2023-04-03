/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._symboltable.ArcBasisSymbolTablePass3Delegator;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.microsoft.z3.Context;
import montiarc._symboltable.MontiArcSymbolTableCompleter;
import montiarc._symboltable.MontiArcSymbolTableCompleterDelegator;
import montiarc._symboltable.MontiArcSymbolTablePass3Delegator;
import montiarc.check.MontiArcCompTypeExprDeSer;
import montiarc.evaluation.MontiArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class MontiArcMill extends MontiArcMillTOP {

  protected static MontiArcMill millMontiArcSymbolTableCompleter;

  protected static MontiArcMill millMontiArcSymbolTableCompleterDelegator;

  protected static MontiArcMill millMontiArcSymbolTablePass3Delegator;

  protected static MontiArcMill millMontiArcFullPrettyPrinter;

  protected static MontiArcMill millMontiArcFullConverter;

  protected static MontiArcMill millCompTypeExprDeSer;

  public static MontiArcSymbolTableCompleter symbolTableCompleter() {
    if (millMontiArcSymbolTableCompleter == null) {
      millMontiArcSymbolTableCompleter = getMill();
    }
    return millMontiArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected MontiArcSymbolTableCompleter _symbolTableCompleter() {
    return new MontiArcSymbolTableCompleter();
  }

  public static MontiArcSymbolTableCompleterDelegator symbolTableCompleterDelegator() {
    if (millMontiArcSymbolTableCompleterDelegator == null) {
      millMontiArcSymbolTableCompleterDelegator = getMill();
    }
    return millMontiArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected MontiArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new MontiArcSymbolTableCompleterDelegator();
  }

  public static MontiArcSymbolTablePass3Delegator symbolTablePass3Delegator()  {
    if (millMontiArcSymbolTablePass3Delegator == null) {
      millMontiArcSymbolTablePass3Delegator = getMill();
    }
    return millMontiArcSymbolTablePass3Delegator._symbolTablePass3Delegator();
  }

  protected MontiArcSymbolTablePass3Delegator _symbolTablePass3Delegator() {
    return new MontiArcSymbolTablePass3Delegator();
  }

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millMontiArcFullPrettyPrinter == null) {
      millMontiArcFullPrettyPrinter = getMill();
    }
    return millMontiArcFullPrettyPrinter._fullConverter(context);
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new MontiArcDeriveSMTExpr(context);
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new MontiArcCompTypeExprDeSer();
  }

  public static void reset() {
    MontiArcMillTOP.reset();
    millMontiArcSymbolTableCompleter = null;
    millMontiArcSymbolTableCompleterDelegator = null;
    millMontiArcFullPrettyPrinter = null;
    millMontiArcFullConverter = null;
  }
}