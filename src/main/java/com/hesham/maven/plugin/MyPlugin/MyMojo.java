package com.hesham.maven.plugin.MyPlugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.hesham.maven.virgo.VirgoPlanBuilder;
import com.hesham.maven.virgo.jaxb.plan.Plan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * 
 * @goal virgo-plan
 * 
 */
public class MyMojo extends AbstractMojo {
//@aggregator true
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;
	
	/**
	 * Packaging
	 * 
	 * @parameter expression="${project.packaging}"
	 * @required
	 */
	private String packaging;

	/**
	 * Plan Configuration, supplied by the parent POM only
	 * 
	 * @parameter expression="${virgo-plan.plan}"
	 * @required
	 */
	private Plan planEntity;
	
	
	/**
	 * Project's base directory
	 * 
	 * @parameter expression="${project.basedir}"
	 * @required
	 */
	private File basedir;
	
	/**
	 * Project's base directory
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 */
	private String artifactFinalName;

	
	/**
	 * 
	 * This is set as static for later children sub modules appending to it 
	 * 
	 */
	private static File barePlanFile;

	String errorStrMissingMandatoryFields = "One or more mandatory configuration wasn't set for the plan file. " +
			"Check that you have specified plan.name, plan.version and that you have already sub-modules specified for this POM.";

	public void execute() throws MojoExecutionException, MojoFailureException {
		VirgoPlanBuilder.logger = getLog();
		
		// Get the packaging type of the artifact in hand, if it is POM then this means it is the parent POM, and in this case a bare plan file should be prepared and written
		if (packaging.equalsIgnoreCase("pom")){
			// Write bare plan file
			
			// Check the required attributes inside the plan xml configuration supplied in the pom
			if (planEntity.getName() == null || planEntity.getVersion() == null
					|| planEntity.getArtifact() == null)
				throw new MojoFailureException(errorStrMissingMandatoryFields);

			String planFileName = artifactFinalName + ".plan";

			// Prepare configuration entity for the VirgoPlanBuilder
			// I am NOT ignoring any other configuration passed (like supplying some artifacts by hand in the parent pom file), it will be added to the plan and any other sub modules will be appended to the same file
			
			getLog().info("Generating a bare Eclipse Virgo plan file...");
			boolean isSuccessful = VirgoPlanBuilder.buildBarePlanS(planEntity, planFileName, outputDirectory.getAbsolutePath());
			if (isSuccessful == false)
				throw new MojoFailureException("A problem occured during building/writing the bare plan file.");
			
			barePlanFile = new File(outputDirectory + File.separator + planFileName);
			
			getLog().info("Bare Eclipse Virgo plan file has been generated. Watch this file as sub module(s) definitions are appended to it.");
		}
		else
		{
			// Then this artifact is actually a submodule, and an entry for it should be added to the main plan file located at the parent/target directory
			String artifactFullName = artifactFinalName + "." + packaging;

			// Check if the current plugin execution is following previous one on the parent, or that the POM for this module is independently being built
			if (barePlanFile == null){
				// Which means it is not executed through the parent
				// Do nothing, you should exit
				getLog().info("Found that this module is not being built through its parent project, thus skipping the virgo-plan plugin.");
				return;
			}
			VirgoPlanBuilder.appendToPlan(artifactFullName, outputDirectory.getAbsolutePath(), barePlanFile);
		}
	}
}
