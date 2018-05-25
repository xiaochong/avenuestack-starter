package avenuestack;

import avenuestack.impl.netty.AvenueStackImpl;
import avenuestack.impl.netty.AvenueStackImpl4Spring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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

    private final Logger logger = LoggerFactory.getLogger(AvenueStackConfiguration.class);

    private final Environment env;
    private final AvenueStackProperties avenueStackProperties;
    private final ApplicationContext applicationContext;
    private final ResourcePatternResolver resourcePatternResolver;

    public AvenueStackConfiguration(Environment env, AvenueStackProperties avenueStackProperties, ApplicationContext applicationContext, ResourcePatternResolver resourcePatternResolver) {
        Assert.notNull(env, "env must be not null!");
        Assert.notNull(avenueStackProperties, "avenueStackProperties must be not null!");
        Assert.notNull(applicationContext, "applicationContext must be not null!");
        Assert.notNull(resourcePatternResolver, "resourcePatternResolver must be not null!");
        this.env = env;
        this.avenueStackProperties = avenueStackProperties;
        this.applicationContext = applicationContext;
        this.resourcePatternResolver = resourcePatternResolver;
    }


    @Bean(destroyMethod = "close")
    protected AvenueStackImpl avenueStack() {
        try {
            AvenueStackImpl4Spring a = new AvenueStackImpl4Spring();
            String profile = env.getProperty("spring.profiles.active");
            if (profile == null || profile.isEmpty()) {
                profile = "default";
            }
            a.setProfile(profile);
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
            a.start();
            logger.info(appName + " started");
            return a;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Bean
    protected AvenueHandler avenueHandler() {
        return new AvenueHandler(applicationContext, avenueStack());
    }

}
