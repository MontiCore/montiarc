<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
this.${prefixes.subcomp()}${ast.getName()} = new <@Util.getCompTypeString ast.getSymbol().getType() "${suffixes.component()}${suffixes.builder()}"/>() <#-- TODO this does not include features -->
    .setName("${ast.getName()}")
    .setScheduler(this.getScheduler())
    <#list helper.getArgNamesMappedToExpressions(ast) as name, expression>
        .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${prettyPrinter.prettyprint(expression)})
    </#list>
    .build();