package com.example.demo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import javax.sql.DataSource;
import java.util.Base64;

@Configuration
@ConditionalOnClass(DataSource.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class MySQLAutoconfiguration {

  private final Region region = Region.EU_WEST_3;
  private final SsmClient ssmClient = SsmClient.builder()
      .region(region)
      .build();
  private final AWSSecretsManager secretsManager = AWSSecretsManagerClientBuilder.standard()
      .withRegion(region.id())
      .build();

  @Bean
  @ConditionalOnMissingBean
  public DataSource dataSource(DataSourceProperties dataSourceProperties,
                               @Value("${app.database.urlPropName}") String urlPropName,
                               @Value("${app.database.usernamePropName}") String usernamePropName,
                               @Value("${app.database.secretName}") String secretName) {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
    dataSource.setUrl(getFromParameterStore(urlPropName));
    dataSource.setUsername(getFromParameterStore(usernamePropName));
    dataSource.setPassword(getSecret(secretName));
    return dataSource;
  }

  public String getSecret(String secretName) {
    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
        .withSecretId(secretName);
    GetSecretValueResult getSecretValueResult = secretsManager.getSecretValue(getSecretValueRequest);
    if (getSecretValueResult.getSecretString() != null) {
      return getSecretValueResult.getSecretString();
    } else {
      return new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
    }
  }

  public String getFromParameterStore(String parameterName) {
    GetParameterRequest build = GetParameterRequest.builder()
        .name(parameterName)
        .build();
    GetParameterResponse parameter = ssmClient.getParameter(build);
    return parameter.parameter().value();
  }
}
