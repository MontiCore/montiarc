<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
${tc.signature("mode")}
<#assign type>${ast.getSymbol().getType().printFullName()}${suffixes.component()}</#assign>
<#assign fieldName>${prefixes.mode()}${mode.getName()}_${prefixes.subcomp()}${ast.getName()}</#assign>
protected ${type} ${fieldName}() {
  return this.${fieldName};
}