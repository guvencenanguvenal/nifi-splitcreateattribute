# nifi-splitcreateattribute

Apache Nifi Split Attribute and than Create New Attribute With Split Values

# Get Started! :sunglasses:

Download [stable version](https://github.com/guvencenanguvenal/nifi-splitcreateattribute/releases/tag/stable).

Build project 

```yaml
mvn clean install
```

After build success, COPY `split-create-attribute.nar` file from `target` directory to `{NIFI_PATH}/lib` directory.


# How to Use?

 -  After copy nar file, restart nifi and you can add SplitCreateAttribute processor like other processors.
 -  You must fill properties.
 
**Attribute Name to be Split**

Property is a Attribute Name which has data that will be split with seperator.

**Seperator**

Property is a regex to split specified attribute.

**Split Attributes Names**

Property is new attribute's names that creating with split datas.

Example: ATTRIBUTENAME1, ATTRIBUTENAME2, ATTRIBUTENAME3

# Example

 - We suppose that your attribute has a data like "12|13|14".
 - Your seperator should be "\\\|" because it's a regex.
 - And then your split attributes name should be like "Name1, Name2, Name3"
 
 **if your attribute name count is not equal your spliting data count, processor will has an error.**
 
# Contribute

Please fork project [nifi-splitcreateattribute](https://github.com/guvencenanguvenal/nifi-splitcreateattribute/fork)


 


