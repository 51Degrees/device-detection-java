# 51Degrees Device Detection Engines

![51Degrees](https://51degrees.com/img/logo.png?utm_source=github&utm_medium=repository&utm_content=readme_main&utm_campaign=java-open-source "Data rewards the curious") **Java Device Detection**

[Developer Documentation](https://51degrees.com/device-detection-java/index.html?utm_source=github&utm_medium=repository&utm_content=documentation&utm_campaign=java-open-source "developer documentation")

## Introduction

This repository contains the device detection engines for the Java implementation of the Pipeline API.

## Pre-requisites

- JDK 8 or later.

- Git Large File System (LFS) for sub module `device-detection-cxx\device-detection-data`

### Data

The Java API can either use our cloud service to get its data or it can use a local (on-premise) copy of the data.

#### Cloud

You will require [resource keys](https://51degrees.com/documentation/4.3/_info__resource_keys.html)
to use the Cloud API, as described on our website. Get resource keys from
our [configurator](https://configure.51degrees.com/), see our [documentation](https://51degrees.com/documentation/4.3/_concepts__configurator.html) on 
how to use this.

#### On-Premise

If you are using 
on-premise detection, a "Lite" version of the data required is packaged 
in this repository. It contains only a limited set of "essential" device detection 
properties. 

You may want to license our complete
data file containing all properties. [Details of our licenses](https://51degrees.com/pricing) are available 
on our website.

## Installation

Our latest release is available as compiled JARs on Maven - or you can compile from source as described below.

### Maven

The 51Degrees Java Device Detection package is available on maven. Make sure to select
the [latest version](https://mvnrepository.com/artifact/com.51degrees/device-detection).

```xml
<!-- Make sure to select the latest version from https://mvnrepository.com/artifact/com.51degrees/pipeline.device-detection -->
<dependency>
    <groupId>com.51degrees</groupId>
    <artifactId>device-detection</artifactId>
    <version>4.3.14</version>
</dependency>
```

This package includes the Cloud and on-premise APIs.

On Windows platform, make sure to install `C++ Redistributable latest 14.2* or above`. This is required to use the Windows native binaries shipped with the Maven package.


### Build and Install from source.

Make sure you have installed the build pre-requisites as described above.

Clone this repository and navigate to the root of the local copy.

### Build dependencies

On Windows make sure you have the Java 8 JDK installed, Maven, Visual Studio 2019 and the latest Windows 10 SDK.
- Minimum required Platform Toolset version is `v142`
- Minimum required Windows SDK version is `10.0.18362.0`

On Linux/macOS make sure you have the Java 8 JDK installed, Maven and Gcc.

Maven version 3.8.4 or higher is recommended, and what is used for our own build.

Batch script and Bash script are provided to support building native binaries on Windows and Linux/macOS.
These scripts are implicitly called by the Maven build step.

### Submodules

Run the following from the Git terminal to obtain all sub-modules.

```
git submodule update --init --recursive 
```


#### Tests

You will need [resource keys](https://51degrees.com/documentation/4.3/_info__resource_keys.html)
(see above) to complete the tests and run examples which include exercising the cloud API.

To verify the code:

```
mvn clean test -DTestResourceKey=[Resource Key]
```

#### Install JARs

```
mvn clean install
```

#### Windows Specific

On Windows, the default Platform Toolset version is `v142` and the default Windows 10 SDK version is `10.0.18362.0`. However these can be overwritten when running `mvn` by adding following options:
- `-DplatformToolsetVersion=[ Platform Toolset Version]`
- `-DwindowsSDKVersion=[ Windows 10 SDK Version ]`

## Projects

- **device-detection** - This is the project to get all Device Detection capabilities.
- **device-detection.hash.engine.on-premise** - when you want to use local detection.
- **device-detection.cloud** - when you want to use our cloud detection.
- **device-detection.shared** - Shared classes.

The following examples are not distributed as maven jars and need to be built by you, please
see the respective README for these projects:

- **device-detection.examples** - Device detection getting started and other introductory examples.
- **device-detection.shell.examples** - Device detection examples to be run from the command line.
