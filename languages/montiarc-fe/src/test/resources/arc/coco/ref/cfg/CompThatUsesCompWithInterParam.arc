package cfg;

component CompThatUsesCompWithInterParam {
    
    component CompWithInterParam<String>(new Impl());
}
