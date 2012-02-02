package com.hesham.maven.virgo;
import java.util.List;

import com.hesham.maven.virgo.jaxb.plan.Plan;


public class VirgoPlanConfig {
	
	private String basePath;
	private String planFileName;
	private List<String> modulesNames;
	private Plan planEntity;
	private String outputDirectoryName;
	
	// Defaults
	{
		outputDirectoryName = "target/";
	}
	
	
	public VirgoPlanConfig(String basePath, String planFileName, Plan planEntity){
		this.basePath = basePath;
		this.planFileName = planFileName;
		this.planEntity = planEntity;
	}
	
	
	public VirgoPlanConfig(String basePath, String planFileName, Plan planEntity, List<String> modulesNames) {
		this.basePath = basePath;
		this.planFileName = planFileName;
		this.planEntity = planEntity;
		this.modulesNames = modulesNames;
	}
	
	
	public Plan getPlanEntity() {
		return planEntity;
	}


	public void setPlanEntity(Plan planEntity) {
		this.planEntity = planEntity;
	}



	public String getPlanFileName() {
		return planFileName;
	}


	public void setPlanFileName(String planFileName) {
		this.planFileName = planFileName;
	}


	public List<String> getModulesNames() {
		return modulesNames;
	}


	public void setModulesNames(List<String> modulesNames) {
		this.modulesNames = modulesNames;
	}


	public String getBasePath() {
		return basePath;
	}


	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}


	public String getOutputDirectoryName() {
		return outputDirectoryName;
	}


	public void setOutputDirectoryName(String outputDirectoryName) {
		this.outputDirectoryName = outputDirectoryName;
	}
	
}
