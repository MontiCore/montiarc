${tc.params("String type", "String name")}

    private ${type} ${name?uncap_first};
    
    protected ${type} get${name?cap_first}() {
        ${type} _sc = this.${name?uncap_first};
        <#-- ${op.includeTemplates(getSubcomponentHook, ast)} -->
        return _sc;
    }
