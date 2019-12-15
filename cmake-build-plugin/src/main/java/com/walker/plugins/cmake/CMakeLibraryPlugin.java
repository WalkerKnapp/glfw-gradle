package com.walker.plugins.cmake;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

// Recreated from org.gradle.samples.plugins.cmake
public class CMakeLibraryPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("com.walker.plugins.wrapped-native");

        final CMakeExtension extension = project.getExtensions().create("cmake", CMakeExtension.class, project.getLayout(), project.getObjects());

        TaskContainer tasks = project.getTasks();

        final TaskProvider<CMake> cmakeDebug = tasks.register("cmakeDebug", CMake.class, task -> {
            task.setBuildType("Debug");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("cppLinkDebug"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
            task.getProjectDirectory().set(extension.getProjectDirectory());
        });

        final TaskProvider<CMake> cmakeRelease = tasks.register("cmakeRelease", CMake.class, task -> {
            task.setBuildType("RelWithDebInfo");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("cppLinkRelease"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getProjectDirectory().set(extension.getProjectDirectory());
        });

        final TaskProvider<Make> assembleDebug = tasks.register("assembleDebug", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the debug binaries");
            task.generatedBy(cmakeDebug);
            task.binary(extension.getBinary());
            task.getProjectDirectory().set(extension.getProjectDirectory());
        });

        TaskProvider<Make> assembleRelease = tasks.register("assembleRelease", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the release binaries");
            task.generatedBy(cmakeRelease);
            task.binary(extension.getBinary());
        });

        tasks.named("assemble", task -> task.dependsOn(assembleDebug));

        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("headers").getOutgoing().artifact(extension.getIncludeDirectory());
        configurations.getByName("linkDebug").getOutgoing().artifact(assembleDebug.flatMap(it -> it.getBinary()));
        configurations.getByName("linkRelease").getOutgoing().artifact(assembleRelease.flatMap(it -> it.getBinary()));
    }
}