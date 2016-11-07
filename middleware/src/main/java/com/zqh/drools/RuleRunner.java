package com.zqh.drools;

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

        String ruleFile = "/Users/zhengqh/Github/tutorials/middleware/src/main/resources/drools/person.drl";
        String rule = "drools/person.drl";
        Object[] facts = {
                new Person("Jon Doe", 21),
                new Person("Jon Doo", 22)
        };

        runner.runRules(rule,facts);

        //--------------------------
        ///Users/zhengqh/Github/tutorials/middleware/target/classes/drools/person.drl
        System.out.println(ClassLoader.getSystemClassLoader().getResource(rule).getPath());

    }

    public void runRules(String ruleFile, Object[] facts) {
        KieServices kieServices = KieServices.Factory.get();
        KieResources kieResources = kieServices.getResources();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieRepository kieRepository = kieServices.getRepository();

        Resource resource = kieResources.newClassPathResource(ruleFile);
        if(ruleFile.startsWith("/")){
            resource = kieResources.newFileSystemResource(ruleFile);
        }

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