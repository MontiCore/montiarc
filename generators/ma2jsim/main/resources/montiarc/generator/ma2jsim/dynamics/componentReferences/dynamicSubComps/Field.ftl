<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
${tc.signature("mode")}
protected ${ast.getSymbol().getType().printFullName()}${suffixes.component()}
  ${prefixes.mode()}${mode.getName()}_${prefixes.subcomp()}${ast.getName()};