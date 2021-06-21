/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

/**
 * Replaces {@link CD4CodeSymbolTableCompleter} in Version 7.0.
 * This should be obsolete in Version 7.1
 */
@Deprecated
public class CD4CBetterCompleter {
  protected CD4CodeTraverser traverser;
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4CBetterCompleter(ASTCDCompilationUnit ast) {
    this(ast.getMCImportStatementList(), ast.isPresentMCPackageDeclaration() ? ast.getMCPackageDeclaration().getMCQualifiedName() : MCQualifiedNameFacade.createQualifiedName(""));
  }

  public CD4CBetterCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = (new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code())).setImports(imports).setPackageDeclaration(packageDeclaration);
    ((CD4CodeGlobalScope) CD4CodeMill.globalScope()).setSymbolTableHelper(this.symbolTableHelper);
    this.traverser = CD4CodeMill.traverser();
    CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(this.symbolTableHelper);
    this.traverser.add4CDBasis(cDBasisVisitor);
    this.traverser.add4OOSymbols(cDBasisVisitor);
    CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(this.symbolTableHelper);
    this.traverser.add4CDAssociation(cDAssociationVisitor);
    this.traverser.setCDAssociationHandler(cDAssociationVisitor);
    CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor = new CDInterfaceAndEnumSymbolTableCompleter(this.symbolTableHelper);
    this.traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CBetterBasisCompleter(this.symbolTableHelper);
    this.traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
  }

  public CD4CodeTraverser getTraverser() {
    return this.traverser;
  }
}