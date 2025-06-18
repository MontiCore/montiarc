<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="behavior" type="arcbasis._ast.ASTArcBehaviorElement" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("behavior")}

@Override
public boolean isDelayed() {
  return ${behavior.isDelayed()?c};
}
