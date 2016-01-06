package ca.redtoad.loadprop;

import java.lang.reflect.Field;
import java.util.Optional;

class FieldPropertySetter extends ReflectionPropertySetter implements PropertySetter {
    private final Field field;

    public FieldPropertySetter(Field field) {
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    protected void setPropertyInternal(Object pojo, Object value) {
        try {
            field.set(pojo, value);
        } catch (IllegalAccessException e) {
            throw new PropertiesLoaderException("Property " + getName() + " can't be set.", e);
        }
    }

    public Class<?> getTargetClass() {
        Class<?> fieldClass = field.getType();
        if (isOptional()) {
            return getParameterizedType(field.getGenericType());
        } else {
            return fieldClass;
        }
    }

    public boolean isOptional() {
        return field.getType().equals(Optional.class);
    }

    public String toString() {
        return "FieldPropertySetter(" + getName() + ")";
    }
}
