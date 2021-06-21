/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

/**
 * fixes a bug that exists in CD4C; this is necessary only for Version 7.0
 */
@Deprecated
public class CD4CBetterBasisCompleter extends CD4CodeBasisSymbolTableCompleter {

  public CD4CBetterBasisCompleter(CDSymbolTableHelper symbolTableHelper) {
    super(symbolTableHelper);
  }

  @Override
  protected void initialize_CD4CodeEnumConstant(ASTCD4CodeEnumConstant ast) {
    FieldSymbol symbol = ast.getSymbol();
    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);
    String enumName = this.symbolTableHelper.getCurrentCDTypeOnStack();
    SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, symbol.getEnclosingScope().getEnclosingScope());
    symbol.setType(typeObject);
  }
}