package com.zqh.rule.drools;

/**
 * Created by zhengqh on 15/8/13.
 */
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class RuleRunner {

    public static void main(String[] args) {
        RuleRunner runner = new RuleRunner();

        String rule = "/Users/zhengqh/Github/tutorials/middleware/src/main/resources/drools/person.drl";
        Object[] facts = {
                new Person("Jon Doe", 21),
                new Person("Jon Doo", 20)
        };

        runner.runRules(rule,facts);
    }

    public void runRules(String ruleFile, Object[] facts) {
        KieServices kieServices = KieServices.Factory.get();
        KieResources kieResources = kieServices.getResources();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieRepository kieRepository = kieServices.getRepository();

        //Resource resource = kieResources.newClassPathResource("src/main/resources/drools/"+ruleFile);
        Resource resource = kieResources.newFileSystemResource(ruleFile);
        kieFileSystem.write("src/main/resources/drools/"+ruleFile, resource);
        //kieFileSystem.write(ruleFile, resource);

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        KieSession kSession = kContainer.newKieSession();

        for (Object fact : facts) {
            kSession.insert(fact);
        }

        kSession.fireAllRules();
    }

}