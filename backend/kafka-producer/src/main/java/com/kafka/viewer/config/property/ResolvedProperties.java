package com.kafka.viewer.config.property;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ResolvedProperties extends Properties {

    private Set<PropertyResolver> resolvers = new HashSet<>();

    public void registerPropertyResolver(PropertyResolver propertyResolver) {
        resolvers.add(propertyResolver);
    }


    @Override
    public synchronized Object put(Object key, Object value) {
        if (!(key instanceof String) || !(value instanceof String)) {
            return super.put(key, value);
        }

        final String resolvedValue = resolvers
                .stream()
                .findFirst()
                .map(resolver -> resolver.resolveProperty((String) key, (String) value))
                .orElse((String) value);

        return super.put(key, resolvedValue);
    }
}
