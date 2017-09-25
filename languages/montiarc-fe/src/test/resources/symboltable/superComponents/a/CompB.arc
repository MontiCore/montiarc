package a;

import b.SuperComponentOtherPackage;

component CompB extends SuperComponentOtherPackage {
    port
        in String str,
        out Integer int3;
}