package avenuestack;

import avenuestack.impl.netty.AvenueStackImpl4Spring;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.nio.file.Paths;

/**
 * @author chenjinquan.jim
 */
@Configuration
@ConditionalOnMissingBean({AvenueStack.class})
@EnableConfigurationProperties(AvenueStackProperties.class)
public class AvenueStackConfiguration {

    private final Environment env;
    private final AvenueStackProperties avenueStackProperties;
    private final ResourcePatternResolver resourcePatternResolver;

    public AvenueStackConfiguration(Environment env, AvenueStackProperties avenueStackProperties, ResourcePatternResolver resourcePatternResolver) {
        Assert.notNull(env, "env must be not null!");
        Assert.notNull(avenueStackProperties, "avenueStackProperties must be not null!");
        Assert.notNull(resourcePatternResolver, "resourcePatternResolver must be not null!");
        this.env = env;
        this.avenueStackProperties = avenueStackProperties;
        this.resourcePatternResolver = resourcePatternResolver;
    }


    @Bean
    protected AvenueStack avenueStack() {
        try {
            AvenueStackImpl4Spring a = new AvenueStackImpl4Spring();
            a.setEnv(env);
            String appName = avenueStackProperties.getAppName();
            a.setDataDir(Paths.get(avenueStackProperties.getDataDir(), appName).toAbsolutePath().toString());
            Resource[] resources = resourcePatternResolver.getResources(avenueStackProperties.getConfDir() + "/*");
            for (Resource resource : resources) {
                String path = resource.getFile().getPath();
                if (path.endsWith(".xml")) {
                    a.addAvenueXml(path);
                }
            }
            a.init();
            return a;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
