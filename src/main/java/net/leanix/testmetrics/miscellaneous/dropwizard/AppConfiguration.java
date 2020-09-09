package net.leanix.testmetrics.miscellaneous.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {

    @JsonProperty("basePath")
    public String basePath;

    @Valid
    @NotNull
    @JsonProperty("database")
    public DataSourceFactory database;

}
