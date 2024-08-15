<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
<#-- SubcomponentSymbol subcomponentSym, String modeName (may be empty: "") -->
${tc.signature("subcomponentSym", "modeName")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign subCompType><@Util.getCompTypeString subcomponentSym.getType() suffixes.component()/></#assign>
<#assign modeNamePart>${modeName}<#if modeName?has_content>_</#if></#assign>
<#assign subCompName>${prefixes.subcomp()}${modeNamePart}${subcomponentSym.getName()}${helper.subcomponentVariantSuffix(ast, subcomponentSym)}</#assign>

<#assign variants = helper.getVariants(ast)>
<#assign hasOnlyOneVariant = variants?size == 1>

protected ${subCompType} ${subCompName}() {
  <#if hasOnlyOneVariant>
    return this.${subCompName};
  <#else>
    if (java.util.Set.of(
        <#list helper.getVariantsWithSubcomponent(ast, subcomponentSym) as v> ${helper.variantSuffix(v)} <sep>, </#list>
      ).contains(this.variantID)) {
      return this.${subCompName};
    } else {
      assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
      return null;
    }
  </#if>
}
