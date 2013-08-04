package org.hotcode.hotcode.structure;

import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Method of a class.
 * 
 * @author khotyn 13-6-25 PM3:16
 */
public class HotCodeMethod {

    private int      access;
    private String   name;
    private String   desc;
    private String   signature;
    private String[] exceptions;

    public HotCodeMethod(int access, String name, String desc, String signature, String[] exceptions){
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public int getAccess() {
        return access;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HotCodeMethod that = (HotCodeMethod) o;

        if (access != that.access) {
            return false;
        }

        if (!desc.equals(that.desc)) {
            return false;
        }

        if (!Arrays.equals(exceptions, that.exceptions)) {
            return false;
        }

        if (!name.equals(that.name)) {
            return false;
        }

        if (signature == null && that.signature == null) {
            return true;
        }

        if (signature == null || that.signature == null) {
            return false;
        }

        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = access;
        result = 31 * result + name.hashCode();
        result = 31 * result + desc.hashCode();
        result = 31 * result + (signature == null ? 0 : signature.hashCode());
        result = 31 * result + Arrays.hashCode(exceptions);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
