/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2019 51 Degrees Mobile Experts Limited, 5 Charlotte Close,
 * Caversham, Reading, Berkshire, United Kingdom RG4 7BY.
 *
 * This Original Work is licensed under the European Union Public Licence (EUPL) 
 * v.1.2 and is subject to its terms as set out below.
 *
 * If a copy of the EUPL was not distributed with this file, You can obtain
 * one at https://opensource.org/licenses/EUPL-1.2.
 *
 * The 'Compatible Licences' set out in the Appendix to the EUPL (as may be
 * amended by the European Commission) shall be deemed incompatible for
 * the purposes of the Work and the provisions of the compatibility
 * clause in Article 5 of the EUPL shall not apply.
 * 
 * If using the Work as, or as part of, a network application, by 
 * including the attribution notice(s) required under Article 5 of the EUPL
 * in the end user terms of the application under an appropriate heading, 
 * such notice(s) shall fulfill the requirements of that article.
 * ********************************************************************* */

package fiftyone.pipeline.uachmanual.examples.mvc.configuration;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * @example mvc/configuration/UACHManualExampleMvcConfig.java
 * Spring MVC User Agent Client Hints-Manual example.
 * 
 * In this scenario, the standard Pipeline API web integration is not used.
 * This means that several jobs that the API usually takes care of automatically
 * must be handled manually. For example, setting the HTTP response headers to
 * request user-agent client hints.
 *
 * The source code for this example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/tree/master/web/pipeline.uachmanual.examples.mvc).
 * 
 * This example can be configured to use the 51Degrees cloud service or a local 
 * data file. If you don't already have data file you can obtain one from the 
 * [device-detection-data](https://github.com/51Degrees/device-detection-data) 
 * GitHub repository.
 * 
 * To use the cloud service you will need to create a **resource key**. 
 * The resource key is used as short-hand to store the particular set of 
 * properties you are interested in as well as any associated license keys 
 * that entitle you to increased request limits and/or paid-for properties.
 * 
 * You can create a resource key using the 51Degrees [Configurator](https://configure.51degrees.com).
 *
 * 1. Set up configuration options to add elements to the 51Degrees Pipeline.
 * ```{xml}
 * <PipelineOptions>
 *     <Elements>
 *         <Element>
 *             <BuildParameters>
 *                 <EndPoint>https://cloud.51degrees.com/api/v4</EndPoint>
 *                 <!-- Obtain a resource key for free at
 *                 https://configure.51degrees.com
 *                 Make sure to include the 'BrowserName','BrowserVendor',
 *                 'BrowserVersion','HardwareName','HardwareVendor',
 *                 'PlatformName','PlatformVendor','PlatformVersion'
 *                 properties as they are used by this example. -->
 *                 <ResourceKey>!!YOUR_RESOURCE_KEY!!</ResourceKey>
 *             </BuildParameters>
 *             <BuilderName>CloudRequestEngine</BuilderName>
 *         </Element>
 *         <Element>
 *             <BuilderName>DeviceDetectionCloudEngine</BuilderName>
 *         </Element>
 *         <Element>
 *             <BuilderName>SetHeadersElement</BuilderName>
 *         </Element>
 *     </Elements>
 * </PipelineOptions>
 * ```
 *
 * Alternatively, to use the on-premise API with automatic updates enabled,
 * replace the cloud element in the config with the new configuration.
 * ```{xml}
 * <PipelineOptions>
 *     <Elements>
 *         <Element>
 *             <BuildParameters>
 *                 <AutoUpdate>true</AutoUpdate>
 *                 <DataFileSystemWatcher>false</DataFileSystemWatcher>
 *                 <CreateTempDataCopy>true</CreateTempDataCopy>
 *                 <!-- Obtain your own license key and enterprise data file
 *                 from https://51degrees.com. -->
 *                 <DataUpdateLicenseKey>[[Your License Key]]</DataUpdateLicenseKey>
 *                 <DataFile>D:\[[Path to data file]]\51Degrees-EnterpriseV4.1.hash</DataFile>
 *                 <PerformanceProfile>LowMemory</PerformanceProfile>
 *             </BuildParameters>
 *             <BuilderName>DeviceDetectionHashEngineBuilder</BuilderName>
 *         </Element>
 *         <Element>
 *             <BuilderName>SetHeadersElement</BuilderName>
 *         </Element>
 *     </Elements>
 * </PipelineOptions>
 * ```
 * 
 * 2. Set up MVC, enable configuration, Add builders and the Pipeline to the Controller.
 * 
 * ```{java}
 * 
 *	// Create the configuration object from an XML file
 *	ServletContext context = request.getSession().getServletContext();
 *	File configFile = new File(context.getRealPath("/WEB-INF/51Degrees-Hash.xml"));
 *
 *	JAXBContext jaxbContext = JAXBContext.newInstance(PipelineOptions.class);
 *	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 *	PipelineOptions options = (PipelineOptions) unmarshaller.unmarshal(configFile);
 *	
 *	// Build a new Pipeline from the configuration.
 *	Pipeline pipeline = new FiftyOnePipelineBuilder()
 *	    .buildFromConfiguration(options);
 *     ...
 * ```
 *
 *
 * 3. Process the request, set response headers and send the results to the view.
 *
 * ```{java}
 *     ...
 *  // Create an instance of WebRequestEvidenceServiceCore and Set evidence 
 *  // from HTTP request headers to flowData
 *  WebRequestEvidenceServiceCore evidenceService = new WebRequestEvidenceServiceCore.Default();
 *  evidenceService.addEvidenceFromRequest(flowData, request);
 *  flowData.process();
 *
 *  // Create an  instance of UACHServiceCore and Set UACH response headers.
 *  UACHServiceCore uachServiceCore = new UACHServiceCore.Default();
 *  uachServiceCore.setResponseHeaders(flowData, (HttpServletResponse)response);
 *     ...
 * ```
 * 
 * 4. Display device details in the view.
 * 
 * ```{java}
 *     ...
 * <strong>Detection results:</strong><br /><br />
 * Hardware Vendor:  ${hardwareVendor.hasValue() ? hardwareVendor.getValue() : "Unknown: ".concat(hardwareVendor.getNoValueMessage())}<br />
 *
 *     ...
 * ```	    
 */

/**
 * MVC Example.
 */
@EnableWebMvc
@Configuration
@ComponentScan({"fiftyone.pipeline.uachmanual.examples.mvc"})
public class UACHManualExampleMvcConfig extends WebMvcConfigurerAdapter {

    public UACHManualExampleMvcConfig() {
        super();
    }
    
    @Autowired
    ServletContext servletContext;

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();

        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/views/");
        bean.setSuffix(".jsp");

        return bean;
    }

}
