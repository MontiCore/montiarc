<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected <@Util.getTypeString field.getType()/> ${prefixes.field()}${field.getName()}${helper.fieldVariantSuffix(ast, field)} = ${prettyPrinter.prettyprint(helper.getInitialForVariable(field))};
