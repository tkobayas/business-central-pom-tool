package com.redhat.gss.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PomFullChecker {

    private static final String POM = "pom.xml";
    private static final String PATH_TO_CHECK = "path-to-check.txt";

    private List<String> kieProjects = new ArrayList<String>();
    private List<String> dashbuilderProjects = new ArrayList<String>();
    private List<String> uberfireProjects = new ArrayList<String>();

    private List<String> pathList = new ArrayList<String>();

    private static final String KIE_VERSION = "6.5.0.Final-redhat-16-RHBRMS-3095";
    private static final String DASHBUILDER_VERSION = "0.5.0.Final-redhat-12-RHBRMS-3095";
    private static final String UBERFIRE_VERSION = "0.9.0.Final-redhat-12-RHBRMS-3095";

    public static void main(String[] args) {
        PomFullChecker pomTool = new PomFullChecker();
        pomTool.work();

        System.out.println("--- finish");
    }

    public PomFullChecker() {
        kieProjects.add("jbpm-console-ng-workbench-integration-client");

        kieProjects.add("drools-wb-drl-text-editor-client");
        kieProjects.add("drools-wb-dsl-text-editor-client");
        kieProjects.add("drools-wb-dtable-xls-editor-client");
        kieProjects.add("drools-wb-enum-editor-client");
        kieProjects.add("drools-wb-globals-editor-client");
        kieProjects.add("drools-wb-guided-dtable-editor-client");
        kieProjects.add("drools-wb-guided-dtree-editor-client");
        kieProjects.add("drools-wb-guided-rule-editor-client");
        kieProjects.add("drools-wb-guided-scorecard-editor-client");
        kieProjects.add("drools-wb-guided-template-editor-client");
        kieProjects.add("drools-wb-scorecard-xls-editor-client");
        kieProjects.add("drools-wb-test-scenario-editor-client");
        kieProjects.add("drools-wb-workitems-editor-client");

        kieProjects.add("kie-wb-common-data-modeller-client");
        kieProjects.add("kie-wb-common-default-editor-client");
        kieProjects.add("kie-wb-common-java-editor-client");
        kieProjects.add("kie-wb-common-ui");
        kieProjects.add("kie-wb-metadata-widget");

        kieProjects.add("kie-wb-common-services-backend");

        kieProjects.add("optaplanner-wb-solver-editor-client");

        uberfireProjects.add("uberfire-commons-editor-client");
        uberfireProjects.add("uberfire-runtime-plugins-client");
        uberfireProjects.add("uberfire-widgets-commons");

        dashbuilderProjects.add("dashbuilder-dataset-editor");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/" + PATH_TO_CHECK)))) {
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!line.endsWith("/")) {
                    throw new RuntimeException(line);
                }
                pathList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void work() {

        for (String path : pathList) {
            Path pomPath = Paths.get(path + POM);
            String line = null;
            try (BufferedReader br = Files.newBufferedReader(pomPath)) {
                boolean inDependencies = false;
                while (br.ready()) {
                    line = br.readLine();

                    if (line.contains("<dependencies>")) {
                        inDependencies = true;
                        continue;
                    } else if (line.contains("</dependencies>")) {
                        inDependencies = false;
                        continue;
                    }

                    if (inDependencies) {
                        if (line.contains("<artifactId>")) {
                            String artifactId = line.trim().replace("<artifactId>", "").replace("</artifactId>", "");
                            if (kieProjects.contains(artifactId)) {
                                checkVersion(KIE_VERSION, br);
                            } else if (uberfireProjects.contains(artifactId)) {
                                checkVersion(UBERFIRE_VERSION, br);
                            } else if (dashbuilderProjects.contains(artifactId)) {
                                checkVersion(DASHBUILDER_VERSION, br);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                throw new RuntimeException("path = " + path + ", line = " + line, e);
            }
        }

    }

    private void checkVersion(String expectedVersion, BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line.contains("version")) {
            String version = line.trim().replace("<version>", "").replace("</version>", "");
            if (version.equals(expectedVersion)) {
                return;
            } else {
                throw new RuntimeException("worng version! : version = " + version);
            }
        } else {
            throw new RuntimeException("no version!");
        }
    }
}
