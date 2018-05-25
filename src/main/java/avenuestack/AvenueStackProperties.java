package avenuestack;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenjinquan.jim
 */
@ConfigurationProperties(prefix = "avenue.stack")
public class AvenueStackProperties {

    private String appName = "change-it";
    private String dataDir = "/opt/data";
    private String confDir = "classpath:/avenue_conf";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getConfDir() {
        return confDir;
    }

    public void setConfDir(String confDir) {
        this.confDir = confDir;
    }
}
