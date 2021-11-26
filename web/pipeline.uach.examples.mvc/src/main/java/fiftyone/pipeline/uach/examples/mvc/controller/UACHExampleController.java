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

package fiftyone.pipeline.uach.examples.mvc.controller;

import fiftyone.devicedetection.hash.engine.onpremise.flowelements.DeviceDetectionHashEngine;
import fiftyone.devicedetection.shared.DeviceData;
import fiftyone.pipeline.cloudrequestengine.flowelements.CloudRequestEngine;
import fiftyone.pipeline.core.data.EvidenceKeyFilter;
import fiftyone.pipeline.core.data.FlowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import fiftyone.pipeline.web.mvc.components.FlowDataProvider;

import javax.servlet.http.HttpServletRequest;

import static fiftyone.pipeline.uach.examples.mvc.controller.UACHExampleHelper.tryGet;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class UACHExampleController {
    private FlowDataProvider flowDataProvider;

    @Autowired
    public UACHExampleController(FlowDataProvider flowDataProvider) {
        this.flowDataProvider = flowDataProvider;
    }

	/*
	 * Controller method that takes evidences from the request
	 * Sets required properties in as model attributes
	 * And return view name.
	 * @param model: Model in which values will be set.
	 * @param request: A request parameter
	 * @param response: A response parameter
	 */
    @RequestMapping(method = RequestMethod.GET)
    public String get(ModelMap model, HttpServletRequest request) throws Exception {

        // A try-with-resource block MUST be used for the FlowData instance.
        // This ensures that native resources created by the device 
        // detection engine are freed.
        try (FlowData data = flowDataProvider.getFlowData(request);) {
        
        	// Get the device data from flowData
        	DeviceData device = data.get(DeviceData.class);

        	model.addAttribute("hardwareVendor",
				tryGet(() -> device.getHardwareVendor()));
        	model.addAttribute("hardwareName",
				tryGet(() -> device.getHardwareName()));
        	model.addAttribute("deviceType",
				tryGet(() -> device.getDeviceType()));
        	model.addAttribute("platformVendor",
				tryGet(() -> device.getPlatformVendor()));
        	model.addAttribute("platformName",
				tryGet(() -> device.getPlatformName()));
        	model.addAttribute("platformVersion",
				tryGet(() -> device.getPlatformVersion()));
        	model.addAttribute("browserVendor",
				tryGet(() -> device.getBrowserVendor()));
        	model.addAttribute("browserName",
				tryGet(() -> device.getBrowserName()));
        	model.addAttribute("browserVersion",
				tryGet(() -> device.getBrowserVersion()));
 
        	// Get evidence based on the device detection engine being used.
        	Map<String, Object> evidenceUsed = new HashMap<>();      	
        	Object engine = null;
        	EvidenceKeyFilter evidenceKeyfilter = null;
        	if(data.getPipeline().getElement(DeviceDetectionHashEngine.class) != null) {
        		engine = (DeviceDetectionHashEngine) data.getPipeline().getElement(DeviceDetectionHashEngine.class);
        		evidenceKeyfilter = ((DeviceDetectionHashEngine) engine).getEvidenceKeyFilter();
        	}
        	else {
       		 engine = (CloudRequestEngine) data.getPipeline().getElement(CloudRequestEngine.class);
       		 evidenceKeyfilter = ((CloudRequestEngine) engine).getEvidenceKeyFilter();
       	    } 
        	
        	for (Map.Entry<String, Object> evidence : data.getEvidence().asKeyMap().entrySet()) {
        		if(evidenceKeyfilter.include(evidence.getKey())) {
        			evidenceUsed.put(evidence.getKey(), evidence.getValue());
        		}
        	}
      	 
        	model.addAttribute("evidenceUsed", evidenceUsed);
        	
        	return "uachexample";
        }
    }
}