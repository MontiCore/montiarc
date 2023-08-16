<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
this.${prefixes.subcomp()}${ast.getName()} = new ${ast.getSymbol().getType().printFullName()}${suffixes.component()}${suffixes.builder()}() <#-- TODO this does not include features -->
    .setName("${ast.getName()}")
    <#list helper.getArgNamesMappedToExpressions(ast) as name, expression>
        .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${prettyPrinter.prettyprint(expression)})
    </#list>
    .build();