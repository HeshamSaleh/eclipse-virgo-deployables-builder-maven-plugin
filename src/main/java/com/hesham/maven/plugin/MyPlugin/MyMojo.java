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
import com.hesham.maven.virgo.VirgoPlanConfig;
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
 * @aggregator true
 */
public class MyMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Modules
	 * 
	 * @parameter expression="${project.modules}"
	 * @required
	 * @readonly
	 */
	private ArrayList<String> modules;

	/**
	 * Modules
	 * 
	 * @parameter expression="${virgo-plan.plan}"
	 * @required
	 */
	private Plan plan;
	
	
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
	 * @parameter expression="${project.artifactId}"
	 * @required
	 */
	private String artifactName;
	
	/**
	 * Project's base directory
	 * 
	 * @parameter expression="${project.version}"
	 * @required
	 */
	private String artifactVersion;
	

	String errorStrMissingMandatoryFields = "One or more mandatory configuration wasn't set for the plan file. " +
			"Check that you have specified plan.name, plan.version and that you have already sub-modules specified for this POM.";

	public void execute() throws MojoExecutionException, MojoFailureException {
		// Check the required attributes inside the plan xml
		if (plan.getName() == null || plan.getVersion() == null
				|| plan.getArtifact() == null || modules == null
				|| modules.size() == 0)
			throw new MojoFailureException(errorStrMissingMandatoryFields);

		getLog().info("Generating the virgo-plan file... ");
		String planFileName = artifactName + "-" + artifactVersion + ".plan";
		
		// Prepare configuration entity for the VirgoPlanBuilder
		VirgoPlanConfig config; 
		// Check if module names were specified inside the plan configuration it self
		if (plan.getArtifact() != null && plan.getArtifact().size() > 0)
			config = new VirgoPlanConfig(basedir.getAbsolutePath()+File.separator, planFileName, plan);
		// Else get the modules from the parent (current) POM
		config = new VirgoPlanConfig(basedir.getAbsolutePath()+File.separator, planFileName, plan, modules);
		
		VirgoPlanBuilder virgoPlanBuilder = new VirgoPlanBuilder(config, getLog());
		boolean isSuccessful = virgoPlanBuilder.buildPlan();
		if (isSuccessful == false)
			throw new MojoFailureException("A problem occured during building/writing the plan file.");
	}
}
