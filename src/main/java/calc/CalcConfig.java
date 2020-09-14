package calc;

import org.apache.kafka.common.config.ConfigDef;
import io.confluent.rest.RestConfig;

public class CalcConfig extends RestConfig {
  private static ConfigDef config;

  static {
    config = baseConfigDef();
  }

  public CalcConfig() {
    super(config);
  }
}
