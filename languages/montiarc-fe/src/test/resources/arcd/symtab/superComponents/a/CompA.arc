package a;

component CompA extends SuperComponentSamePackage {
    port
        in String sth,

        // overwrite a port from the super component
        in Integer s1;

}