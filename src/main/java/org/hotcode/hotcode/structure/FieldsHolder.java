package org.hotcode.hotcode.structure;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * A data structure that holds the fields that added to the class.
 * 
 * @author khotyn 2013-06-24 20:22:27
 */
public class FieldsHolder {

    private Map<String, Object> fields = new HashMap<String, Object>();

    public Object getField(String fieldKey) {
        return fields.get(fieldKey);
    }

    public void addField(String fieldKey, Object value) {
        fields.put(fieldKey, value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
