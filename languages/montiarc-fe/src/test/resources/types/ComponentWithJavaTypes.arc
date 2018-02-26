package types;

component ComponentWithJavaTypes {
    port
        in SuperType supIn1,
        in SubType subIn1,
        in SuperType supIn2,
        in SubType subIn2,
        out SuperType supOut,
        out SubType subOut;
}