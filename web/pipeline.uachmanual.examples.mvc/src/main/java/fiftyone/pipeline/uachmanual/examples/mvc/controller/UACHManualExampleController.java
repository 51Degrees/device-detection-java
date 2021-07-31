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

package fiftyone.pipeline.uachmanual.examples.mvc.controller;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.core.configuration.PipelineOptions;
import fiftyone.pipeline.core.data.FlowData;
import fiftyone.pipeline.core.flowelements.Pipeline;
import fiftyone.pipeline.engines.fiftyone.flowelements.FiftyOnePipelineBuilder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fiftyone.pipeline.web.services.UACHServiceCore;
import fiftyone.pipeline.web.services.WebRequestEvidenceServiceCore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

@Controller
@RequestMapping("/")
public class UACHManualExampleController {
    
	/*
	 * Controller method that takes evidences from the request
	 * Sets required properties in as model attributes
	 * And return view name.
	 * @param model: Model in which values will be set.
	 * @param request: A request parameter
	 * @param response: A response parameter
	 */
    @RequestMapping(method = RequestMethod.GET)
    public String get(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
       
    	// Create the configuration object from an XML file
        ServletContext context = request.getSession().getServletContext();
        File configFile = new File(context.getRealPath("/WEB-INF/51Degrees-Hash.xml"));
        
        JAXBContext jaxbContext = JAXBContext.newInstance(PipelineOptions.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        
        // Bind the configuration to a pipeline options instance
        PipelineOptions options = (PipelineOptions) unmarshaller.unmarshal(configFile);

        // Build a new Pipeline from the configuration.
        Pipeline pipeline = new FiftyOnePipelineBuilder()
            .buildFromConfiguration(options);

        // A try-with-resource block MUST be used for the FlowData instance.
        // This ensures that native resources created by the device 
        // detection engine are freed.
        try (FlowData flowData = pipeline.createFlowData()) {
        
        // Create an instance of WebRequestEvidenceServiceCore
        WebRequestEvidenceServiceCore evidenceService = new WebRequestEvidenceServiceCore.Default();
        
        // Set evidence from HTTP request headers to flowData
        evidenceService.addEvidenceFromRequest(flowData, request);
        
        // Start processing the data
        flowData.process();
        
        // Get the device data from flowData
        DeviceData device = flowData.get(DeviceData.class);

        // Create an  instance of UACHServiceCore
        UACHServiceCore uachServiceCore = new UACHServiceCore.Default();
        
        // Set UACH response headers.
        uachServiceCore.setResponseHeaders(flowData, (HttpServletResponse)response);

        model.addAttribute("hardwareVendor", device.getHardwareVendor());
        model.addAttribute("hardwareName", device.getHardwareName());
        model.addAttribute("deviceType", device.getDeviceType());
        model.addAttribute("platformVendor", device.getPlatformVendor());
        model.addAttribute("platformName", device.getPlatformName());
        model.addAttribute("platformVersion", device.getPlatformVersion());
        model.addAttribute("browserVendor", device.getBrowserVendor());
        model.addAttribute("browserName", device.getBrowserName());
        model.addAttribute("browserVersion", device.getBrowserVersion());

    	Map<String, Object> evidenceUsed = new HashMap<>();      	
    	DeviceDetectionHashEngine engine = flowData.getPipeline().getElement(DeviceDetectionHashEngine.class);
    	for (Map.Entry<String, Object> evidence : flowData.getEvidence().asKeyMap().entrySet()) {
    		if(engine.getEvidenceKeyFilter().include(evidence.getKey())) {
    			evidenceUsed.put(evidence.getKey(), evidence.getValue());
    		}
    	}
    	model.addAttribute("evidenceUsed", evidenceUsed);
    	
        return "uachmanualexample";
    }
  }
}