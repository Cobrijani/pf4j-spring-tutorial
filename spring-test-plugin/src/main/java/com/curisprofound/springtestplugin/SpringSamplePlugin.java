package com.curisprofound.springtestplugin;

import com.curisprofound.plugins.PluginInterface;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class SpringSamplePlugin extends SpringPlugin {

    private static final Logger log = LoggerFactory.getLogger(SpringSamplePlugin.class);

    public SpringSamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        ObjectMapper om = new ObjectMapper();
        Version version = om.version();
        log.info("Spring Sample plugin.start()");
        log.info("Jackson version: {}", version);
    }

    @Override
    public void stop() {
        log.info("Spring Sample plugin.stop()");
        super.stop(); // to close applicationContext
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(ApplicationConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Extension(ordinal = 1)
    public static class SpringPlugin implements PluginInterface {

        @Autowired
        private GreetProvider greetProvider;

        @Override
        public String identify() {
            return greetProvider.provide();
        }

        @Override
        public List<Object> mvcControllers() {
            return Arrays.asList(new PluginController());
        }

        @Override
        public List<RouterFunction<?>> reactiveRoutes() {
            return Arrays.asList(route(GET("/plugin-end-point"),
                    req -> ServerResponse.ok().body(Mono.just("reactive router endpoint"), String.class)));
        }
    }
}
