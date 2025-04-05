package com.github.tedtang054.union;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/23 13:39
 */
@Configuration
@ComponentScan("com.github.tedtang054")
@EnableScheduling
public class TransportApplication {

    public static void main(String[] args) throws Exception {
        var context = new AnnotationConfigApplicationContext();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        var sources = loader.load("application.yml", new ClassPathResource("application.yml"));
        context.getEnvironment().getPropertySources().addFirst(sources.get(0));
        context.register(TransportApplication.class);
        context.refresh();
        callRunners(context);
    }

    private static void callRunners(ApplicationContext context) {
        DefaultApplicationArguments args = new DefaultApplicationArguments();
        List<Object> runners = new ArrayList<>();
        runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
        runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
        AnnotationAwareOrderComparator.sort(runners);
        for (Object runner : new LinkedHashSet<>(runners)) {
            if (runner instanceof ApplicationRunner) {
                callRunner((ApplicationRunner) runner, args);
            }
            if (runner instanceof CommandLineRunner) {
                callRunner((CommandLineRunner) runner, args);
            }
        }
    }

    private static void callRunner(ApplicationRunner runner, ApplicationArguments args) {
        try {
            (runner).run(args);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to execute ApplicationRunner", ex);
        }
    }

    private static void callRunner(CommandLineRunner runner, ApplicationArguments args) {
        try {
            (runner).run(args.getSourceArgs());
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to execute CommandLineRunner", ex);
        }
    }

}
