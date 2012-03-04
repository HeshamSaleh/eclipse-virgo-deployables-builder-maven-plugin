package com.hesham.maven.virgo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.plugin.logging.Log;

import com.hesham.maven.virgo.jaxb.plan.ArtifactType;
import com.hesham.maven.virgo.jaxb.plan.ObjectFactory;
import com.hesham.maven.virgo.jaxb.plan.Plan;


public class VirgoPlanBuilder {
	
//	private VirgoPlanConfig planConfig;
	
	static String SEP = File.separator;
	
	public static Log logger ;
	
	public static boolean buildBarePlanS(Plan planEntity, String planFileFullName,  String outputDirectory){
		return writeBarePlanFile(planEntity, planFileFullName, outputDirectory);
	}
	
	/**
	 * @param artifactFullFinalName Full final name including the extension
	 * @param artifactOutputDirectory
	 * @param planFile
	 */
	public static boolean appendToPlan(String artifactFullFinalName, String artifactOutputDirectory, File planFile){
		// Load this plan file and get the Plan XML object
		Plan planEntity = readPlanFile(planFile);
		
		// Add the artifact Entry to it
		ArtifactType artifactTypeEntry = getArtifactInfo(artifactFullFinalName, artifactOutputDirectory);
		planEntity.getArtifact().add(artifactTypeEntry);
		logger.info("Prepared a plan entry for the artifact's file " + artifactFullFinalName);
		
		// Rewrite the file again
		return writePlanFile(planEntity, planFile);
	}
	
	
	public static ArtifactType getArtifactInfo(String artifactFullFinalName, String artifactOutputDirectory){
		String artifactPath = artifactOutputDirectory + SEP + artifactFullFinalName; 
		try {

			ArtifactType artifact = new ArtifactType();

			JarFile deployableJar = new JarFile(artifactPath);
			
			// Get its Manifest 
			Manifest deployableJarManifest = deployableJar.getManifest();
			String moduleSymbolicName = deployableJarManifest.getMainAttributes().getValue("Bundle-SymbolicName").trim();
			String moduleVersion = deployableJarManifest.getMainAttributes().getValue("Bundle-Version").trim();
			String moduleType = "bundle"; // TODO: Inspect if there are any other artifact types
			
			// Set the Artifact with values gotten from the original Manifest 
			artifact.setName(moduleSymbolicName);
			artifact.setVersion(moduleVersion);
			artifact.setType(moduleType); 
			
			return artifact;
			
		} catch (FileNotFoundException e) {
			logger.error("The module located at " + artifactPath + " couldn't be found, however the building will be continued.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean writeBarePlanFile(Plan planEntity, String planFileFullName,  String outputDirectory){
		// Write the plan JAXB to a file
		try {
			JAXBContext context = JAXBContext.newInstance("com.hesham.maven.virgo.jaxb.plan");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE);
			marshaller.setProperty("jaxb.schemaLocation","http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd");
			JAXBElement<Plan> planElemWrapper = new ObjectFactory().createPlan(planEntity);
			
			String filePath = outputDirectory + SEP + planFileFullName;
			
			// By default, this should be the target directory
			File outputDir = new File(outputDirectory);
			if ( ! outputDir.exists())
			{
				logger.info("Didn't find a target directory, creating one...");
				outputDir.mkdir();
			}
			
			FileWriter fileWriter = new FileWriter(new File(filePath));
			marshaller.marshal(planElemWrapper, fileWriter);
			fileWriter.close();
			return true;
			
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean writePlanFile(Plan updatedPlan, File planFile){
		// Write the plan JAXB to a file
		try {
			JAXBContext context = JAXBContext.newInstance("com.hesham.maven.virgo.jaxb.plan");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE);
			marshaller.setProperty("jaxb.schemaLocation","http://www.eclipse.org/virgo/schema/plan http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd");
			JAXBElement<Plan> planElemWrapper = new ObjectFactory().createPlan(updatedPlan);
			
			FileWriter fileWriter = new FileWriter(planFile);
			marshaller.marshal(planElemWrapper, fileWriter);
			fileWriter.close();
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static Plan readPlanFile(File planFile){
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance("com.hesham.maven.virgo.jaxb.plan");
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		try {
			return ((JAXBElement<Plan>)unmarshaller.unmarshal(planFile)).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Log getLogger() {
		return logger;
	}

	public void setLogger(Log logger) {
		this.logger = logger;
	}
}

