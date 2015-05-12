# BMD File Format Reference

# 1. Design goals

* Can be written sequentially without the need to keep records in memory.

* Optimized for writing (not reading, as that can be done on a PC with more resources).

* Flat structure, no more nested records like in HPROF.

* Records allowed to be written in any order (no need to define strings before referencing them).

# 2. Optimizations

* Remapping of ids (starting at 0 for more efficient storage)

* Use varints ([variable length integers](http://en.wikipedia.org/wiki/Numeral_system#Generalized_variable-length_integers)) for all int/long fields

# 3. File format

The BMD file format is defined as one single header followed by any number of records. The ordering of these records is not defined and any order should be accepted by an application reading it. Although this might require additional passes when reading a file it also means that a BMD file can be created without the need to keep a large portion of the memory dump in RAM so that dependencies can be resolved before writing the output

.

## 3.1 Header

<table>
  <tr>
    <td>varint32</td>
    <td>Version</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Length of metadata</td>
  </tr>
  <tr>
    <td>byte[]</td>
    <td>Metadata (can be used to store any arbitrary data related to the dump).</td>
  </tr>
</table>


## 3.2 Records

The header is followed by a number of records, each one of them starting with a varint32 identifying the record type (also known as the tag).

### 3.2.1 String

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 1)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>String id (unique identifier for this string, ids can overlap with object ids).</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Length of string data in bytes</td>
  </tr>
  <tr>
    <td>byte[]</td>
    <td>String data (string as bytes, assumed to be UTF-8 encoded)</td>
  </tr>
</table>


**_Example:_**

<table>
  <tr>
    <td>01</td>
    <td>9803</td>
    <td>17</td>
    <td>6a6176612e6c616e672e7265662e5265666572656e6365</td>
  </tr>
  <tr>
    <td>Tag</td>
    <td>String id (408)</td>
    <td>Data length (23)</td>
    <td>Data ("java.lang.ref.Reference")</td>
  </tr>
</table>


### 3.2.2 Hashed string

<table>
  <tr>
    <td>varintt32</td>
    <td>Tag (value = 2)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>String id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>String data length in bytes</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>String hashcode (calculated using String.hashCode())</td>
  </tr>
</table>


**_Example:_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>02</td>
    <td>76</td>
    <td>24</td>
    <td>9a81ec9a01</td>
  </tr>
  <tr>
    <td>Tag (2)</td>
    <td>String id (118)</td>
    <td>Length (36)</td>
    <td>Hash (324731034)</td>
  </tr>
</table>


### 3.2.3 Class definition

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 3)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Class id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Super class id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Name string id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Constant count</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Constant index</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Constant type (see 3.2)</td>
  </tr>
  <tr>
    <td>→ byte[]</td>
    <td>Constant data (length depending on Type)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Static field count</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Field name string id</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Type (see 3.2)</td>
  </tr>
  <tr>
    <td>→ byte[]</td>
    <td>Static field data (length depending on Type)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Instance field count</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Field name string id</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Type (see 3.2)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Discarded instance field size (in bytes)</td>
  </tr>
</table>


**_Example:_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>03</td>
    <td>34</td>
    <td>35</td>
    <td>a842</td>
  </tr>
  <tr>
    <td>Tag (3)</td>
    <td>Class id (52)</td>
    <td>Super class id (53)</td>
    <td>Name string id (8488)</td>
  </tr>
  <tr>
    <td>00</td>
    <td>02</td>
    <td>e169</td>
    <td>00</td>
  </tr>
  <tr>
    <td>Constant count (0)</td>
    <td>Static count (2)</td>
    <td>Name (13537)</td>
    <td>Type (0 = Object)</td>
  </tr>
  <tr>
    <td>36</td>
    <td>c58302</td>
    <td>07</td>
    <td>a2c5f6d6b4829b85e901</td>
  </tr>
  <tr>
    <td>Field data</td>
    <td>Name (33221)</td>
    <td>Type (7)</td>
    <td>Field data</td>
  </tr>
  <tr>
    <td>00</td>
    <td>00</td>
    <td></td>
    <td></td>
  </tr>
  <tr>
    <td>Instance fields (0)</td>
    <td>Skipped (0)</td>
    <td></td>
    <td></td>
  </tr>
</table>


### 3.2.4 Instance dump

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 4)</td>
  </tr>
  <tr>
    <td>varin32</td>
    <td>Object id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Class id</td>
  </tr>
  <tr>
    <td>byte[]</td>
    <td>Instance data (length is based on the class definition)</td>
  </tr>
</table>


**_Example:_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>04</td>
    <td>b901</td>
    <td>be01</td>
    <td>00</td>
  </tr>
  <tr>
    <td>Tag</td>
    <td>Object id (185)</td>
    <td>Class id (190)</td>
    <td>Instance data</td>
  </tr>
</table>


### 3.2.5 Root objects

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 5)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Root count</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Object id</td>
  </tr>
</table>


**_Example:_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>05</td>
    <td>02</td>
    <td>f053r, e971</td>
  </tr>
  <tr>
    <td>Tag (5)</td>
    <td>Root count (2)</td>
    <td>Object ids (10736, 14569)</td>
  </tr>
</table>


### 3.2.6 Object Array

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 6)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Object id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Element class id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Element count</td>
  </tr>
  <tr>
    <td>→ varint32</td>
    <td>Element object ids</td>
  </tr>
</table>


**_Example_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>06</td>
    <td>d203</td>
    <td>d603</td>
    <td>02</td>
    <td>00,00</td>
  </tr>
  <tr>
    <td>Tag (6)</td>
    <td>Object id (466)</td>
    <td>Element class (470)</td>
    <td>Count (2)</td>
    <td>Elements (null, null)</td>
  </tr>
</table>


### 3.2.7 Primitive array placeholder

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (value = 7)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Object id</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Element field type (see 3.2)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Element count</td>
  </tr>
</table>


**_Example_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>07</td>
    <td>9201</td>
    <td>03</td>
    <td>d801</td>
  </tr>
  <tr>
    <td>Tag (7)</td>
    <td>Object id (146)</td>
    <td>Element type (3)</td>
    <td>Count (216)</td>
  </tr>
</table>


### 3.2.8 Legacy record

Contains a standard HPROF record wrapped inside the *Record data *field. Used for HPROF records of either unknown or not supported type.

<table>
  <tr>
    <td>varint32</td>
    <td>Tag (8)</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Original record tag</td>
  </tr>
  <tr>
    <td>varint32</td>
    <td>Record length</td>
  </tr>
  <tr>
    <td>byte[]</td>
    <td>Record data</td>
  </tr>
</table>


**_Example_**

*Please note that the example shows the decoded varint values and not the actual sequence of bytes stored.*

<table>
  <tr>
    <td>08</td>
    <td>05</td>
    <td>12</td>
    <td>000000000000000000000000</td>
  </tr>
  <tr>
    <td>Tag (8)</td>
    <td>Original tag (5)</td>
    <td>Record length (12)</td>
    <td>Data</td>
  </tr>
</table>


## 3.2 Field types

<table>
  <tr>
    <td>0</td>
    <td>object</td>
  </tr>
  <tr>
    <td>1</td>
    <td>int</td>
  </tr>
  <tr>
    <td>2</td>
    <td>boolean</td>
  </tr>
  <tr>
    <td>3</td>
    <td>byte</td>
  </tr>
  <tr>
    <td>4</td>
    <td>char</td>
  </tr>
  <tr>
    <td>5</td>
    <td>float</td>
  </tr>
  <tr>
    <td>6</td>
    <td>double</td>
  </tr>
  <tr>
    <td>7</td>
    <td>long</td>
  </tr>
  <tr>
    <td>8</td>
    <td>short</td>
  </tr>
</table>


