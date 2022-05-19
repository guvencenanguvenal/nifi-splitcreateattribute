package com.gcg.nifi.processors;

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
@Tags({"split attribute", "split", "attribute"})
@CapabilityDescription("Split existing attribute and create new attribute with split values.")
public class SplitCreateAttribute extends AbstractProcessor {

    private static String SPLIT_NAME_SEPARATOR = "[,]";

    private static ComponentLog log;

    private Set<Relationship> relationships;
    private List<PropertyDescriptor> properties;

    public static final PropertyDescriptor SEPARATOR = new PropertyDescriptor.Builder()
            .name("Split Separator (Regex)")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor ATTRIBUTE_NAME = new PropertyDescriptor.Builder()
            .name("Attribute Name to be Split")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor SPLIT_VALUES_NAME = new PropertyDescriptor.Builder()
            .name("Split Attributes Names")
            .description("Default value is attr[index] but if you want to name new attribute, It's must be a string separate with , (Example: name1,name2,name3)")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();


    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Success relationship")
            .build();

    public static final Relationship FAIL = new Relationship.Builder()
            .name("fail")
            .description("Fail relationship.")
            .build();


    @Override
    public void init(final ProcessorInitializationContext context) {

        log = getLogger();

        List<PropertyDescriptor> properties = new ArrayList();
        properties.add(SEPARATOR);
        properties.add(ATTRIBUTE_NAME);
        properties.add(SPLIT_VALUES_NAME);
        this.properties = Collections.unmodifiableList(properties);

        Set<Relationship> relationships = new HashSet();
        relationships.add(SUCCESS);
        relationships.add(FAIL);
        this.relationships = Collections.unmodifiableSet(relationships);

        log.info("-------------Split and Create Attribute Init OK-------------");
    }

    @Override
    public void onTrigger(ProcessContext context, ProcessSession session)
            throws ProcessException {

        FlowFile flowfile = session.get();

        String separator = context.getProperty(SEPARATOR).getValue();
        String attributeValue = flowfile.getAttribute(context.getProperty(ATTRIBUTE_NAME).getValue());
        String splitValuesName = context.getProperty(SPLIT_VALUES_NAME).getValue();
        String[] splitNames, attributeSplitValues;

        if (separator.isEmpty()) {
            log.error("Separator is empty");
            session.transfer(flowfile, FAIL);
            return;
        }

        if (attributeValue == null) {
            log.error("Attribute is not found!");
            session.transfer(flowfile, FAIL);
            return;
        }

        attributeSplitValues = attributeValue.split(separator, -1);

        if (splitValuesName != null) {
            splitNames = splitValuesName.split(SPLIT_NAME_SEPARATOR, -1);

            if (splitNames.length != attributeSplitValues.length) {
                log.error("Split values and attributes count is not equal!");
                session.transfer(flowfile, FAIL);
                return;
            }
        } else {
            splitNames = new String[attributeSplitValues.length];
            for (int i = 0; i < attributeSplitValues.length; i++){
                splitNames[i] = "attr" + (i + 1);
            }
        }

        for (int i = 0; i < attributeSplitValues.length; i++) {
            String attributeSplitValue = attributeSplitValues[i];

            flowfile = session.putAttribute(flowfile, splitNames[i].trim(), attributeSplitValue);
        }
        session.transfer(flowfile, SUCCESS);
    }

    /*
     *
     * This props is for configuration.
     *
     */
    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return properties;
    }

}
