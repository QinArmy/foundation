package org.qinarmy.foundation.datasource;

public enum DataSourceRole {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    TIMEOUT("timout");

    private final String display;

    DataSourceRole(String display) {
        this.display = display;
    }


    @Override
    public String toString() {
        return this.display;
    }
}
