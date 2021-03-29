# 51Degrees Device Detection Engines

![51Degrees](https://51degrees.com/img/logo.png?utm_source=github&utm_medium=repository&utm_content=readme_main&utm_campaign=java-open-source "Data rewards the curious") **Java Device Detection**

[Developer Documentation](https://51degrees.com/device-detection-java/4.2/index.html?utm_source=github&utm_medium=repository&utm_content=documentation&utm_campaign=java-open-source "developer documentation")

## Introduction

This repository contains the device detection engines for the Java implementation of the Pipeline API.

## Pre-requesites

JDK 7 or later.

Git Large File System (LFS) for sub module `device-detection-cxx\device-detection-data`

### Submodules

Run the following from the Git terminal to obtain all sub modules.

```
git submodule update --init --recursive 
```

### Build dependencies

On Windows make sure you have the Java 7 JDK installed, Maven, MSBuild and the latest Windows 10 SDK.

On linux make sure you have the Java 7 JDK installed, Maven and Gcc.

## Projects

- **device-detection.shared** - Shared classes used by device detection engines.
- **device-detection.pattern.engine.on-premise** - Java implementation of the device detection pattern engine.
- **device-detection.hash.engine.on-premise** - Java implementation of the device detection hash engine.
- **device-detection.cloud** - A Java engine which retreives device detection results by consuming the 51Degrees cloud service.
- **device-detection** - Device detection engines and related projects.
- **device-detection.examples** - Device detection examples.
- **web/pipeline.web.examples.servlet** - A device detection servlet example.
- **web/pipeline.web.examples.mvc** - A device detection web app example which uses the Model-View-Container pattern.

## Examples 

### Device Detection

|Example|Description|Algorithm|
|-------|-----------|---------|
|All Profiles|This examples shows how to obtain all Hardware profiles from the 51Degrees data file.|Pattern|
|Benchmark|Benchmarks popular configurations of the device detection engines.|Hash / Pattern|
|Comparison|Compares multiple device detection methods for accuracy and performance outputting a single CSV file where each row contains the results from one or more solutions for each of the target User-Agents provided.|Hash / Pattern|
|ConfigureCache|This example shows how to implement a Guava cache adaptor and build an on-premise Pattern engine configured to use the Guava cache.|Pattern|
|ConfigureFromFile|This example shows how to build a Pipeline from a configuration file.|Cloud / Hash / Pattern|
|DynamicFilters|This example shows how to implement a filter to only return a set of signatures where the specified property is equal to the specified value.|Pattern|
|GettingStarted|This example uses 51Degrees device detection to determine whether a given User-Agent corresponds to a mobile device or not.|Cloud / Hash / Pattern|
|MatchForProfileId|This example shows how to extract the device id from a device detection result and then use that id to look up the device and extract the value of a property.|Pattern|
|MatchMetrics|This example shows how to get information about the detection result such as the algorithm that was used to perform the detection or the signature rank which indicated how popular the device is.|Hash / Pattern|
|MetaData|This example shows how to get properties from the getProperties() method and how to get the values for each property.|Hash / Pattern|
|OfflineProcessing|This example shows how to process a CSV file containing User-Agent strings and produce an output csv containing the source User-Agent strings with IsMobile, PlatformName and PlatformVersion properties appended.|Hash / Pattern|
|Performance|The examples demonstrates the performance of the HighPerformance device detection configuration.|Hash / Pattern|
|StronglyTyped|This tutorial demonstrates how to return results for the IsMobile property value as a boolean rather than a string.|Hash / Pattern|

### Servlet
Using the 51Degrees cloud service, this example shows how to use 51Degrees Pipeline to extend HttpServlet to provide device detection capabilities. 

### MVC
Using the 51Degrees cloud service, this exmaple shows how use the 51Degrees pipeline with the Model-View-Controller pattern to provide device detection capabilities.

# Build and Test

## Installation

### Maven

The 51Degrees Java Device Detection package is available on maven:

```xml
<!-- Make sure to select the latest version from https://mvnrepository.com/artifact/com.51degrees/pipeline.device-detection -->
<dependency>
    <groupId>com.51degrees</groupId>
    <artifactId>pipeline.device-detection</artifactId>
    <version>4.2.0</version>
    <type>pom</type>
</dependency>
```
This package includes the Cloud, Hash and Pattern APIs and will allow you to switch between them.

### Manual Installation

Make sure you have installed the build pre-requisites.

Clone this respository and navigate to the root and run:

```
mvn install
```

## Tests

The tests use junit, to run them navigate to the root of this repository and call:

```
mvn test
```