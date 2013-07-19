package org.hotcode.hotcode.structure;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author khotyn 13-6-26 PM9:26
 */
public class HotCodeClass {

    private int                access;
    private String             className;
    private Set<HotCodeField>  fields       = new LinkedHashSet<>();
    private Set<HotCodeMethod> constructors = new LinkedHashSet<>();

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public Set<HotCodeMethod> getConstructors() {
        return constructors;
    }

    public void addConstructor(HotCodeMethod constructor) {
        constructors.add(constructor);
    }

    public Set<HotCodeField> getFields() {
        return fields;
    }

    public void addField(HotCodeField field) {
        fields.add(field);
    }

    public boolean hasField(HotCodeField hotCodeField) {
        return fields.contains(hotCodeField);
    }

    public HotCodeField getFieldByName(final String fieldName) {
        Collection<HotCodeField> result = Collections2.filter(fields, new Predicate<HotCodeField>() {

            @Override
            public boolean apply(org.hotcode.hotcode.structure.HotCodeField input) {
                return StringUtils.equals(fieldName, input.getName());
            }
        });

        return result.isEmpty() ? null : result.iterator().next();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
