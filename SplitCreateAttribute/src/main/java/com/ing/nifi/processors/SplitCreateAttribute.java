package com.ing.nifi.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

@SideEffectFree
@Tags({"split attribute", "split"})
@CapabilityDescription("Split And Create New Attribute.")
public class SplitCreateAttribute extends AbstractProcessor {
	
	private static String SPLITNAMESEPERATOR = "[,]";
	
	private static ComponentLog log;
	
	private Set<Relationship> relationships;
	private List<PropertyDescriptor> properties;
	
	public static final PropertyDescriptor SEPERATOR = new PropertyDescriptor.Builder()
    .name("Split Separator")
    .required(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
	
	public static final PropertyDescriptor ATTRBUTENAME = new PropertyDescriptor.Builder()
    .name("Split Attribute Name")
    .required(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
	
	public static final PropertyDescriptor SPLITVALUESNAME = new PropertyDescriptor.Builder()
    .name("Split Values Names")
    .description("It's should be a string seperate with , (Example: name1,name2,name3)")
    .required(true)
    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
    .build();
	
	
	public static final Relationship SUCCESS = new Relationship.Builder()
    .name("success")
    .description("Succes relationship")
    .build();

	public static final Relationship FAIL = new Relationship.Builder()
	.name("fail")
	.description("Fail relationship.")
	.build();
	
	
	@Override
	public void init(final ProcessorInitializationContext context){
		
		log = getLogger();
		
		List<PropertyDescriptor> properties = new ArrayList<>();
	    properties.add(SPLITVALUESNAME);
	    properties.add(ATTRBUTENAME);
	    properties.add(SEPERATOR);
	    this.properties = Collections.unmodifiableList(properties);
		
	    Set<Relationship> relationships = new HashSet<>();
	    relationships.add(SUCCESS);
	    relationships.add(FAIL);
	    this.relationships = Collections.unmodifiableSet(relationships);
	    
	    log.info("-------------Init OK-------------");
	}
	
	@Override
	public void onTrigger(ProcessContext context, ProcessSession session)
			throws ProcessException {
		
		FlowFile flowfile = session.get();
		
		String seperator = context.getProperty(SEPERATOR).getValue();
		String attributeValue = flowfile.getAttribute(context.getProperty(ATTRBUTENAME).getValue());
		String splitvaluesname = context.getProperty(SPLITVALUESNAME).getValue();
		String[] splitNames, attributeSplitValues;
		
		if (seperator.isEmpty()){
			log.error("Separator is empty");
			session.transfer(flowfile, FAIL);
			return;
		}
		
		if (attributeValue == null){
			log.error("Attribute is not valid!");
			session.transfer(flowfile, FAIL);
			return;
		}
		
		splitNames = splitvaluesname.split(SPLITNAMESEPERATOR, -1);
		attributeSplitValues = attributeValue.split("\\" + seperator, -1);
		
		if (splitNames.length == attributeSplitValues.length){
			for (int i = 0; i < attributeSplitValues.length; i++){
				String attributeSplitValue = attributeSplitValues[i];
			
				flowfile = session.putAttribute(flowfile, splitNames[i].trim(), attributeSplitValue);
			}
		}
		else{
			log.error("Split count is not valid!");
			session.transfer(flowfile, FAIL);
			return;
		}
		
		session.transfer(flowfile, SUCCESS);
	
	}
	
	/*
	 *
	 * This props is for configuration.
	 * 
	 */
	@Override
	public Set<Relationship> getRelationships(){
		return relationships;
	}
	
	@Override
	 public List<PropertyDescriptor> getSupportedPropertyDescriptors(){
      return properties;
	 }

}
