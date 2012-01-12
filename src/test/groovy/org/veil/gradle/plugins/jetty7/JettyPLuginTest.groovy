package org.veil.gradle.plugins.jetty7

import static org.hamcrest.CoreMatchers.instanceOf

import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue
import static org.hamcrest.CoreMatchers.is

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test


class JettyPLuginTest {

	private Project project = ProjectBuilder.builder().withProjectDir(new File("build/tmp/tests")).build()
	
	@Test
	void shouldApplyWarPluginAndAddsConventionToProject() {
		new Jetty7Plugin().apply(project)
		
		assertTrue(project.getPlugins().hasPlugin(WarPlugin))
		
		assertThat(project.convention.plugins.jetty7, instanceOf(JettyPluginConvention))
	}
	
	@Test
	void shouldAddTasksToProject() {
		new Jetty7Plugin().apply(project);
		
		Task task = project.tasks[Jetty7Plugin.JETTY_RUN]
		assertThat(task, instanceOf(JettyRun))
		assertThat(task.httpPort, is(project.httpPort))
		assertTrue(isTaskDependsOnOtherTask(task, JavaPlugin.CLASSES_TASK_NAME))
		
		task = project.tasks[Jetty7Plugin.JETTY_RUN_WAR]
		assertThat(task, instanceOf(JettyRunWar))
		assertThat(task.httpPort, is(project.httpPort))
		assertTrue(isTaskDependsOnOtherTask(task, WarPlugin.WAR_TASK_NAME))
		
		task = project.tasks[Jetty7Plugin.JETTY_STOP]
		assertThat(task, instanceOf(JettyStop))
		assertThat(task.stopPort, is(project.stopPort))
	}
	
	@Test
	void shouldAddMappingToNewJettyTask() {
		new Jetty7Plugin().apply(project)
		
		Task task = project.tasks.add('customRun', JettyRun)
		assertTrue(isTaskDependsOnOtherTask(task, JavaPlugin.CLASSES_TASK_NAME))
		assertThat(task.httpPort, is(project.httpPort))
		
		task = project.tasks.add('customWar', JettyRunWar)
		assertTrue(isTaskDependsOnOtherTask(task, WarPlugin.WAR_TASK_NAME))
		assertThat(task.httpPort, is(project.httpPort))
	}
	
	
	private boolean isTaskDependsOnOtherTask(Task task, String otherTaskName) {
		boolean result = false;
		task.getTaskDependencies().getDependencies(task).each {
			dep -> if (dep.name.equals(otherTaskName)) {result = true; return;}
		}
		return result;
	}
}
