<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
${tc.signature("mode")}
<#assign builder>${ast.getSymbol().getType().printFullName()}${suffixes.component()}${suffixes.builder()}</#assign>
<#assign fieldName>${prefixes.mode()}${mode.getName()}_${prefixes.subcomp()}${ast.getName()}</#assign>
this.${fieldName} = new ${builder}()
    .setName("${ast.getName()}")
    <#list helper.getArgNamesMappedToExpressions(ast) as name, expression>
        .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${prettyPrinter.prettyprint(expression)})
    </#list>
    .build();