package io.smallrye.config;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

final class ConfigValueConfigSourceWrapper implements ConfigValueConfigSource, Serializable {
    private final ConfigSource configSource;

    private ConfigValueConfigSourceWrapper(final ConfigSource configSource) {
        this.configSource = configSource;
    }

    @Override
    public ConfigValue getConfigValue(final String propertyName) {
        String value = configSource.getValue(propertyName);
        if (value != null) {
            return ConfigValue.builder()
                    .withName(propertyName)
                    .withValue(value)
                    .withConfigSourceName(getName())
                    .withConfigSourceOrdinal(getOrdinal())
                    .build();
        }

        return null;
    }

    @Override
    public Map<String, ConfigValue> getConfigValueProperties() {
        return new ConfigValueMapStringView(configSource.getProperties(),
                configSource.getName(),
                configSource.getOrdinal());
    }

    @Override
    public Map<String, String> getProperties() {
        return configSource.getProperties();
    }

    @Override
    public String getValue(final String propertyName) {
        return configSource.getValue(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return configSource.getPropertyNames();
    }

    @Override
    public String getName() {
        return configSource.getName();
    }

    @Override
    public int getOrdinal() {
        return configSource.getOrdinal();
    }

    static ConfigValueConfigSource wrap(final ConfigSource configSource) {
        if (configSource instanceof ConfigValueConfigSource) {
            return (ConfigValueConfigSource) configSource;
        } else {
            return new ConfigValueConfigSourceWrapper(configSource);
        }
    }
}
