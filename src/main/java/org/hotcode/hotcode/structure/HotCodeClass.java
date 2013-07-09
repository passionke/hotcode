package org.hotcode.hotcode.structure;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author khotyn 13-6-26 PM9:26
 */
public class HotCodeClass {

    private int               access;
    private String            className;
    private Set<HotCodeField> fields = new LinkedHashSet<>();

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public Set<HotCodeField> getFields() {
        return fields;
    }

    public void setFields(Set<HotCodeField> fields) {
        this.fields = fields;
    }

    public boolean hasField(String name, String desc) {
        return fields.contains(new HotCodeField(0, name, desc));
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
