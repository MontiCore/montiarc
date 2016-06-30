${tc.params("String name")}

    /**
    * Checks constraint ${name}. Override this method to
    * add handwritten implementation. 
    */
    protected boolean _check${name?cap_first}() {
        return true;
    }