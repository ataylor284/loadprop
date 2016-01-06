package ca.redtoad.loadprop;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

abstract class ReflectionPropertySetter implements PropertySetter {

    public void setProperty(Object pojo, Object value) {
        if (isOptional()) {
            setPropertyInternal(pojo, Optional.of(value));
        } else {
            setPropertyInternal(pojo, value);
        }
    }

    public void setEmpty(Object pojo) {
        setPropertyInternal(pojo, Optional.empty());
    }

    protected abstract void setPropertyInternal(Object pojo, Object value);

    protected Class<?> getParameterizedType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                return (Class) typeArguments[0];
            } else {
                throw new PropertiesLoaderException("Property " + getName() + " must be parameterized with a single non-generic type.");
            }
        } else {
            throw new PropertiesLoaderException("Property " + getName() + " must be parameterized with a type.");
        }
    }

}
