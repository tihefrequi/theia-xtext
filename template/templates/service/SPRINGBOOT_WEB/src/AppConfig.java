package {service.namespace};

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class AppConfig {	
	
    @Primary
    @Bean(name="postgresDataSource")
    @ConfigurationProperties(prefix="datasource.postgres")
    public DataSource postgressDataSource() {
        return DataSourceBuilder.create().build();
    }
}
