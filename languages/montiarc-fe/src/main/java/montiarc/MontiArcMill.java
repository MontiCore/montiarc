/* (c) https://github.com/MontiCore/monticore */
package montiarc;

public class MontiArcMill extends MontiArcMillTOP {

  //TODO: Remove once BasicSymbolsMill initializes all required classes.
  public  static  void init ()  {
    mill = new MontiArcMill();
    de.monticore.literals.mccommonliterals.MCCommonLiteralsMill.initMe(new montiarc._auxiliary.MCCommonLiteralsMillForMontiArc());
    de.monticore.expressions.commonexpressions.CommonExpressionsMill.initMe(new montiarc._auxiliary.CommonExpressionsMillForMontiArc());
    de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill.initMe(new montiarc._auxiliary.AssignmentExpressionsMillForMontiArc());
    de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill.initMe(new montiarc._auxiliary.MCSimpleGenericTypesMillForMontiArc());
    de.monticore.statements.mccommonstatements.MCCommonStatementsMill.initMe(new montiarc._auxiliary.MCCommonStatementsMillForMontiArc());
    de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill.initMe(new montiarc._auxiliary.MCVarDeclarationStatementsMillForMontiArc());
    arccore.ArcCoreMill.initMe(new montiarc._auxiliary.ArcCoreMillForMontiArc());
    de.monticore.mcbasics.MCBasicsMill.initMe(new montiarc._auxiliary.MCBasicsMillForMontiArc());
    de.monticore.literals.mcliteralsbasis.MCLiteralsBasisMill.initMe(new montiarc._auxiliary.MCLiteralsBasisMillForMontiArc());
    de.monticore.expressions.expressionsbasis.ExpressionsBasisMill.initMe(new montiarc._auxiliary.ExpressionsBasisMillForMontiArc());
    de.monticore.types.mccollectiontypes.MCCollectionTypesMill.initMe(new montiarc._auxiliary.MCCollectionTypesMillForMontiArc());
    de.monticore.types.mcbasictypes.MCBasicTypesMill.initMe(new montiarc._auxiliary.MCBasicTypesMillForMontiArc());
    de.monticore.statements.mcstatementsbasis.MCStatementsBasisMill.initMe(new montiarc._auxiliary.MCStatementsBasisMillForMontiArc());
    de.monticore.symbols.oosymbols.OOSymbolsMill.initMe(new montiarc._auxiliary.OOSymbolsMillForMontiArc());
    de.monticore.symbols.basicsymbols.BasicSymbolsMillTOP.initMe(new montiarc._auxiliary.BasicSymbolsMillForMontiArc()); //Difference
    arcbasis.ArcBasisMill.initMe(new montiarc._auxiliary.ArcBasisMillForMontiArc());
    comfortablearc.ComfortableArcMill.initMe(new montiarc._auxiliary.ComfortableArcMillForMontiArc());
    genericarc.GenericArcMill.initMe(new montiarc._auxiliary.GenericArcMillForMontiArc());
  }
}