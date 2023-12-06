<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.fields()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

  <#list ast.getFields() as field>
    <@Util.getTypeString field.getSymbol().getType()/> ${prefixes.field()}${field.getName()}();
    void ${prefixes.setterMethod()}${prefixes.field()}${field.getName()}(<@Util.getTypeString field.getSymbol().getType()/> value);
  </#list>
}