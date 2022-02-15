# 51Degrees Device Detection Engines - Shell Examples

![51Degrees](https://51degrees.com/img/logo.png?utm_source=github&utm_medium=repository&utm_content=readme_main&utm_campaign=java-open-source "Data rewards the curious") **Java Device Detection**

[Developer Documentation](https://51degrees.com/device-detection-java/index.html?utm_source=github&utm_medium=repository&utm_content=documentation&utm_campaign=java-open-source "developer documentation")

## Introduction

This module contains examples that are to be run in a Jakarta EE (J2EE) container. We provide several examples
of different approaches to implementation, using various combinations of:

- Use of our Filter `fiftyone.pipeline.web.PipelineFilter` to provide a pipeline
- Creation of your own pipeline and use in a servlet (called "manual")
- Demonstration of detection involving User Agent Client Hints (UACH)
- Use of the cloud and on-premise detection engines
- Use of the Spring Framework MVC model in the Jakarta EE context

The examples may be run using an embedded Tomcat from their `main` method.