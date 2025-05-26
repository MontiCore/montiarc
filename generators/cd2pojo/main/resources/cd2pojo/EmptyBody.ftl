<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="de.monticore.cd4codebasis._ast.ASTCDMethodSignature" -->
<#if !ast.isConstructor() && !ast.getMCReturnType().isPresentMCVoidType()>
  throw new UnsupportedOperationException();
</#if>
