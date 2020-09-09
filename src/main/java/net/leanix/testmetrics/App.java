package net.leanix.testmetrics;

import com.codahale.metrics.Gauge;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.leanix.testmetrics.miscellaneous.dropwizard.AppConfiguration;
import net.leanix.testmetrics.miscellaneous.dropwizard.HibernateModule;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class App extends Application<AppConfiguration> {

    public static final String LIQUIBASE_MIGRATION_FILE = "postgres/migrations.xml";

    private GuiceBundle guiceBundle;
    private HibernateBundle<AppConfiguration> hibernateBundle;

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "testmetrics";
    }

    @Override
    public void initialize(final Bootstrap<AppConfiguration> bootstrap) {
        hibernateBundle = new HibernateBundle<>(
            Object.class
        ) {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.database;
            }
        };
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
            )
        );
        // register hibernate bundle before guice to make sure factory initialized before guice context start
        bootstrap.addBundle(hibernateBundle);

        guiceBundle = GuiceBundle.builder()
            .enableAutoConfig(getClass().getPackage().getName())
            .modules(new HibernateModule(hibernateBundle))
            .build();
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new MigrationsBundle<>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.database;
            }

            @Override
            public String getMigrationsFileName() {
                return LIQUIBASE_MIGRATION_FILE;
            }
        });
    }

    @Override
    public void run(
        final AppConfiguration configuration,
        final Environment environment)
    {
        environment.metrics().register(
            "schemas",
            (Gauge<Object>) () -> hibernateBundle.getSessionFactory().openSession()
                .createSQLQuery("select count(schema_name) from information_schema.schemata").getSingleResult()
        );
    }

    public GuiceBundle getGuiceBundle() {
        return guiceBundle;
    }

    public HibernateBundle<AppConfiguration> getHibernateBundle() {
        return hibernateBundle;
    }
}
