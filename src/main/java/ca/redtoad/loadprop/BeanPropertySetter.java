package ca.redtoad.loadprop;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

class BeanPropertySetter extends ReflectionPropertySetter implements PropertySetter {
    private final PropertyDescriptor propertyDescriptor;

    public BeanPropertySetter(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    public String getName() {
        return propertyDescriptor.getName();
    }

    protected void setPropertyInternal(Object pojo, Object value) {
        try {
            propertyDescriptor.getWriteMethod().invoke(pojo, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PropertiesLoaderException("Property " + getName() + " can't be set.", e);
        }
    }

    public Class<?> getTargetClass() {
        Class<?> propertyClass = propertyDescriptor.getPropertyType();
        if (isOptional()) {
            return getParameterizedType(propertyDescriptor.getWriteMethod().getGenericParameterTypes()[0]);
        } else {
            return propertyClass;
        }
    }

    public boolean isOptional() {
        return propertyDescriptor.getPropertyType().equals(Optional.class);
    }

    public String toString() {
        return "BeanPropertySetter(" + getName() + ")";
    }
}
