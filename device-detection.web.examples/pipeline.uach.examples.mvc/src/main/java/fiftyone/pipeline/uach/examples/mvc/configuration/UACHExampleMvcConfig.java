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

package fiftyone.pipeline.uach.examples.mvc.configuration;

import javax.servlet.ServletContext;

import fiftyone.pipeline.web.mvc.components.FiftyOneInterceptor;
import fiftyone.pipeline.web.mvc.configuration.FiftyOneInterceptorConfig;
import fiftyone.pipeline.web.mvc.configuration.FiftyOneInterceptorConfigDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import static fiftyone.pipeline.web.mvc.components.FiftyOneInterceptor.enableClientsideProperties;

/**
 * @example mvc/configuration/UACHExampleMvcConfig.java
 * 
 * Spring MVC User Agent Client Hints example.
 *
 * @include{doc} example-web-integration-client-hints.txt
 * 
 * In this scenario, the standard Pipeline API web integration is used.
 * This means that several jobs that the API usually takes care of automatically
 * For example, setting the HTTP response headers to request user-agent client hints.
 *
 * The source code for this example is available in full on [GitHub](https://github.com/51Degrees/device-detection-java/tree/master/device-detection.web.examples/pipeline.uach.examples.mvc).
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
 * Set up configuration options to add elements to the 51Degrees Pipeline.
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
 *     </Elements>
 * </PipelineOptions>
 * ```
 * 
 * 2. Set up MVC, enable configuration, and add the Pipeline component.
 * ```{java}
 * @EnableWebMvc
 * @Configuration
 * @ComponentScan({"fiftyone.pipeline.uach.examples.mvc.controller","fiftyone.pipeline.web.mvc"})
 * public class UACHExampleMvcConfig extends WebMvcConfigurerAdapter {
 *     ...
 * ```
 *
 * 3. Configure the interceptor.
 * ```{java}
 * public class UACHExampleMvcConfig extends WebMvcConfigurerAdapter {
 *     ...
 *     @Bean
 *     public FiftyOneInterceptorConfig fiftyOneInterceptorConfig() {
 *         final FiftyOneInterceptorConfigDefault bean = new FiftyOneInterceptorConfigDefault();
 *     
 *         bean.setDataFilePath(servletContext.getRealPath("/WEB-INF/51Degrees-Hash.xml"));
 *         bean.setClientsidePropertiesEnabled(true);
 *     
 *         return bean;
 *     }
 *     ...
 * ```
 *
 * 4. Enable client-side code to improve detection accuracy on devices like iPhones.
 * ```{java}
 * public class UACHExampleMvcConfig extends WebMvcConfigurerAdapter {
 *     ...
 *     @Override
 *     public void addViewControllers(final ViewControllerRegistry registry) {
 *         enableClientsideProperties(registry);
 *     }
 *     ...
 * ```
 *
 * 5. Add the interceptor.
 * ```{java}
 * public class UACHExampleMvcConfig extends WebMvcConfigurerAdapter {
 *     ...
 *     @Autowired
 *     FiftyOneInterceptor fiftyOneInterceptor;
 *     
 *     @Override
 *     public void addInterceptors(final InterceptorRegistry registry) {
 *         registry.addInterceptor(fiftyOneInterceptor);
 *     }
 *     ...
 * ```
 *
 * 6. Inject the `FlowDataProvider` into a controller.
 * ```{java}
 * @Controller
 * @RequestMapping("/")
 * public class UACHExampleController {
 *     private FlowDataProvider flowDataProvider;
 *
 *     @Autowired
 *     public ExampleController(FlowDataProvider flowDataProvider) {
 *         this.flowDataProvider = flowDataProvider;
 *     }
 *     ...
 * ```
 *
 * 7. Use the results contained in the flow data to display something on a page view.
 * ```{java}
 * @Controller
 * @RequestMapping("/")
 * public class UACHExampleController {
 *     ...
 *     @RequestMapping(method = RequestMethod.GET)
 *     public String get(ModelMap model, HttpServletRequest request) {
 *         FlowData data = flowDataProvider.getFlowData(request);
 *         DeviceData device = data.get(DeviceData.class);
 *         model.addAttribute("browser", device.getBrowserVendor() + " " + device.getBrowserName() + " " + device.getBrowserVersion());
 *         model.addAttribute("device", device.getHardwareVendor() + " " + device.getHardwareName());
 *         model.addAttribute("os", device.getPlatformVendor() + " " + device.getPlatformName() + " " + device.getPlatformVersion());
 *
 *         return "example";
 *     }
 *     ...
 * ```
 * ## Controller
 *
 * @include mvc/controller/UACHExampleController.java
 *
 * ## Config
 */

/**
 * MVC Example.
 */
@EnableWebMvc
@Configuration
@ComponentScan({"fiftyone.pipeline.uach.examples.mvc.controller","fiftyone.pipeline.web.mvc"})
public class UACHExampleMvcConfig extends WebMvcConfigurerAdapter {

    public UACHExampleMvcConfig() {
        super();
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        enableClientsideProperties(registry);
    }

    @Autowired
    ServletContext servletContext;

    @Bean
    public FiftyOneInterceptorConfig fiftyOneInterceptorConfig() {
        final FiftyOneInterceptorConfigDefault bean = new FiftyOneInterceptorConfigDefault();

        bean.setDataFilePath(servletContext.getRealPath("/WEB-INF/51Degrees-Hash.xml"));
        bean.setClientsidePropertiesEnabled(true);

        return bean;
    }

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();

        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/views/");
        bean.setSuffix(".jsp");

        return bean;
    }

    @Autowired
    FiftyOneInterceptor fiftyOneInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(fiftyOneInterceptor);
    }
}
