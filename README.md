csv2xml
=======

It's java converter to parse en csv file to xml file

This parser is compatible with [openDataWrapper the lodpaddle wrapper](https://github.com/masterALMA2016/openDataWrapper)


Example of parser:
------------------

Input file
```
name;description;date
element 1;It's my first element;22/10/2014
element 2;;24/10/2014
third element;It's my third element;
```

Output file
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<document>
<data>
<element>
<name>element 1</name>
<description>It's my first element</description>
<date>22/10/2014</date>
</element>
<element>
<name>element 2</name>
<description/>
<date>24/10/2014</date>
</element>
<element>
<name>third element</name>
<description>It's my third element</description>
<date/>
</element>
</data>
</document>
```

Usage:
------

For compile:
```
mvn install
```

launch with jar in target
```
java -jar target/csv2xml-1.0-SNAPSHOT.jar "./test.csv" "./test.xml" ";"
```

