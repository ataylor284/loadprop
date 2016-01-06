package ca.redtoad.loadprop;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertiesLoader {

    private PropertiesLoaderConfiguration config = PropertiesLoaderConfiguration.DEFAULT;

    public <T> T load(Class<T> propertiesClass, String propsString) throws IOException {
        Properties props = new Properties();
        props.load(new StringReader(propsString));
        return load(propertiesClass, props);
    }

    public <T> T load(Class<T> propertiesClass, InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        return load(propertiesClass, props);
    }

    public <T> T load(Class<T> pojoClass, Properties props) {
        try {
            T pojo = pojoClass.newInstance();
            Map<String, PropertySetter> propertySetters = getPropertySettersForClass(pojoClass);

            Set<String> unboundPropNames = new HashSet<String>();
            unboundPropNames.addAll(propertySetters.keySet());
            unboundPropNames.removeAll(config.getPropertiesToIgnore());
            List<String> unmatchedProps = new ArrayList<String>();
            List<String> errorMessages = new ArrayList<String>();

            for (String name : props.stringPropertyNames()) {
                if (propertySetters.containsKey(name)) {
                    PropertySetter propertySetter = propertySetters.get(name);
                    try {
                        propertySetter.setProperty(pojo, cast(name, props.getProperty(name), propertySetter.getTargetClass()));
                    } catch (IllegalArgumentException e) {
                        errorMessages.add("Error setting property " + name + " to value \"" + props.getProperty(name) + "\": " + e);
                    }
                    unboundPropNames.remove(name);
                } else {
                    unmatchedProps.add(name);
                }
            }

            for (Iterator<String> it = unboundPropNames.iterator(); it.hasNext(); ) {
                String unboundPropName = it.next();
                PropertySetter propertySetter = propertySetters.get(unboundPropName);
                if (propertySetter.isOptional()) {
                    propertySetter.setEmpty(pojo);
                    it.remove();
                }
            }

            if (!unboundPropNames.isEmpty()) {
                errorMessages.add("Properties missing from properties file: " +
                                  unboundPropNames.stream().collect(Collectors.joining(", ")) +
                                  ".");
            }
            if (!unmatchedProps.isEmpty()) {
                errorMessages.add("Properties in properties file not bound to properties class: " +
                                  unmatchedProps.stream().collect(Collectors.joining(", ")) +
                                  ".");
            }
            if (!errorMessages.isEmpty()) {
                throw new PropertiesLoaderException(errorMessages.stream().collect(Collectors.joining("\n")));
            }

            return pojo;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new PropertiesLoaderException("Unable to instantiate: " + pojoClass, e);
        } catch (IntrospectionException e) {
            throw new PropertiesLoaderException("Unable to introspect: " + pojoClass, e);
        }
    }

    private Map<String, PropertySetter> getPropertySettersForClass(Class<?> pojoClass) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(pojoClass);

        return Stream.concat(
            Arrays.stream(pojoClass.getFields()).map(f -> new FieldPropertySetter(f)),
            Arrays.stream(beanInfo.getPropertyDescriptors()).map(d -> new BeanPropertySetter(d)))
            .collect(Collectors.toMap(PropertySetter::getName, Function.identity()));
    }

    /**
     * Cast a string value to the given type.
     */
    private <T> T cast(String name, String value, Class<T> targetType) {
        if (targetType == String.class) {
            return (T) value;
        } else if (targetType == Integer.class || targetType == Integer.TYPE) {
            return (T) Integer.decode(cleanNumeric(value));
        } else if (targetType == Byte.class || targetType == Byte.TYPE) {
            return (T) Byte.decode(cleanNumeric(value));
        } else if (targetType == Short.class || targetType == Short.TYPE) {
            return (T) Short.decode(cleanNumeric(value));
        } else if (targetType == Long.class || targetType == Long.TYPE) {
            return (T) Long.decode(cleanNumeric(value));
        } else if (targetType == Float.class || targetType == Float.TYPE) {
            return (T) Float.valueOf(value);
        } else if (targetType == Double.class || targetType == Double.TYPE) {
            return (T) Double.valueOf(value);
        } else if (targetType == Boolean.class || targetType == Boolean.TYPE) {
            return (T) Boolean.valueOf(value);
        } else if (targetType == Character.class || targetType == Character.TYPE) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Input \"" + value + "\" is not of length 1");
            }
            return (T) Character.valueOf(value.charAt(0));
        } else {
            throw new PropertiesLoaderException("Can't convert '" + value + "' to class " + targetType + " for property " + name + ".");
        }
    }

    /**
     * Strip off leading zeros unless in hex format to avoid octal
     * conversions.
     */
    private String cleanNumeric(String value) {
        if (value.contains("0x")) {
            return value;
        } else {
            int leadingZeros = 0;
            while (value.substring(leadingZeros).startsWith("0")) {
                leadingZeros++;
            }
            return value.substring(leadingZeros);
        }
    }
}
