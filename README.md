# nifi-splitcreateattribute

Apache Nifi Split Attribute and than Create New Attribute With Split Values

**Please contribute project.**

# Get Started!

Download project as zip and configure pom.xml and then meven build.

After build copy *.nar file from target to {NIFI_PATH}/lib

# How to Use?

 -  After copy nar file, you can add this processor like other processors.
 -  You should fill the properties.
 
**Attribute Name**

This is a property which has data with seperator.

**Seperator**

This property is regex to split specified attribute.

**Split Attributes Name**

This is a property which is a string array spliting with comma.

Example: ATTRIBUTE1, ATTRIBUTE2, ATTRIBUTE3

# Example

 - We suppose that your attribute has a data like "12|13|14".
 - Your seperator should be "\\\\|" because it's a regex.
 - And then your split attributes name should be like "Name1, Name2, Name3"
 
 **if your attribute name count is not equal your spliting data count, processor will has an error.**

 


