package com.redhat.gss.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PomTool {

    private static final String POM = "pom.xml";
    private static final String POM_NEW = "pom.xml.new";

    private List<String> kieProjects = new ArrayList<String>();
    private List<String> dashbuilderProjects = new ArrayList<String>();
    private List<String> uberfireProjects = new ArrayList<String>();

    private static final String KIE_VERSION = "6.5.0.Final-redhat-16-RHBRMS-3095";
    private static final String DASHBUILDER_VERSION = "0.5.0.Final-redhat-12-RHBRMS-3095";
    private static final String UBERFIRE_VERSION = "0.9.0.Final-redhat-12-RHBRMS-3095";

    public static void main(String[] args) {
        PomTool pomTool = new PomTool();
        pomTool.work();

        System.out.println("--- finish");
    }

    public PomTool() {
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

        uberfireProjects.add("uberfire-commons-editor-client");
        uberfireProjects.add("uberfire-runtime-plugins-client");
        uberfireProjects.add("uberfire-widgets-commons");

        dashbuilderProjects.add("dashbuilder-dataset-editor");
    }

    private void work() {
        Path pomPath = Paths.get(POM);
        Path pomNewPath = Paths.get(POM_NEW);

        try (BufferedReader br = Files.newBufferedReader(pomPath);
                BufferedWriter bw = Files.newBufferedWriter(pomNewPath)) {
            boolean inDependencies = false;
            while (br.ready()) {
                String line = br.readLine();
                bw.write(line + "\n");

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
                            addVersionIfAbsent(KIE_VERSION, br, bw);
                        } else if (uberfireProjects.contains(artifactId)) {
                            addVersionIfAbsent(UBERFIRE_VERSION, br, bw);
                        } else if (dashbuilderProjects.contains(artifactId)) {
                            addVersionIfAbsent(DASHBUILDER_VERSION, br, bw);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addVersionIfAbsent(String version, BufferedReader br, BufferedWriter bw) throws IOException {
        String line = br.readLine();
        if (line.contains("version")) {
            bw.write(line + "\n");
            return;
        } else {
            bw.write("      <version>" + version + "</version>\n");
            bw.write(line + "\n");
            return;
        }
    }
}
