package a;

import b.SuperComponentOtherPackage;

/*
 * Valid model.
 */
component CompB extends SuperComponentOtherPackage {
  port
    in String str,
    out Integer int3;
}