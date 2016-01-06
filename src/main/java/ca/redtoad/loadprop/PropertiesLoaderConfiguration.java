package ca.redtoad.loadprop;

import java.util.Collections;
import java.util.Set;

public class PropertiesLoaderConfiguration {

    public static PropertiesLoaderConfiguration DEFAULT =
        new PropertiesLoaderConfiguration()
        .withPropertiesToIgnore(Collections.singleton("class"));

    private Set<String> propertiesToIgnore;

    public Set<String> getPropertiesToIgnore() {
        return propertiesToIgnore;
    }

    public void setPropertiesToIgnore(Set<String> propertiesToIgnore) {
        this.propertiesToIgnore = propertiesToIgnore;
    }

    public PropertiesLoaderConfiguration withPropertiesToIgnore(Set<String> propertiesToIgnore) {
        setPropertiesToIgnore(propertiesToIgnore);
        return this;
    }

}

