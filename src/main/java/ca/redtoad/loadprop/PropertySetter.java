package ca.redtoad.loadprop;

interface PropertySetter {
    String getName();
    void setProperty(Object pojo, Object value);
    void setEmpty(Object pojo);
    Class<?> getTargetClass();
    boolean isOptional();
}
