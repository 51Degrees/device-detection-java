/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2025 51 Degrees Mobile Experts Limited, Davidson House,
 * Forbury Square, Reading, Berkshire, United Kingdom RG1 3EU.
 *
 * This Original Work is licensed under the European Union Public Licence
 * (EUPL) v.1.2 and is subject to its terms as set out below.
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

package fiftyone.devicedetection.shared;
import fiftyone.pipeline.core.data.types.JavaScript;
import fiftyone.pipeline.engines.data.AspectData;
import fiftyone.pipeline.engines.data.AspectPropertyValue;
import java.util.List;
// This interface sits at the top of the name space in order to make
// life easier for consumers.
/**
 * Interface exposing typed accessors for properties related to a device
 * returned by a device detection engine.
 */
public interface DeviceData extends AspectData
{
	/**
	 * Indicates what ajax request format should be used.
	 */
	AspectPropertyValue<String> getAjaxRequestType();
	/**
	 * Indicates if the browser supports 'window.requestAnimationFrame()' method.
	 */
	AspectPropertyValue<Boolean> getAnimationTiming();
	/**
	 * Indicates the resolution of the device's back camera in megapixels. For a device that has a rotating camera the same value is returned for front and back megapixels properties.
	 */
	AspectPropertyValue<Double> getBackCameraMegaPixels();
	/**
	 * Indicates the capacity of the device's standard battery in mAh. This property is not applicable for a device that does not have a battery.
	 */
	AspectPropertyValue<Integer> getBatteryCapacity();
	/**
	 * Indicates the number of bits used to describe the colour of each individual pixel, also known as bit depth or colour depth.
	 */
	AspectPropertyValue<Integer> getBitsPerPixel();
	/**
	 * Indicates if the browser fully supports BlobBuilder, containing a BlobBuilder interface, a FileSaver interface, a FileWriter interface, and a FileWriterSync interface.
	 */
	AspectPropertyValue<Boolean> getBlobBuilder();
	/**
	 * Refers to the list of audio codecs in specific formats supported for Decode by the Web Browser. This list of codecs is supported for playback on a basic browser installation.
	 */
	AspectPropertyValue<List<String>> getBrowserAudioCodecsDecode();
	/**
	 * Refers to the list of audio codecs in specific formats supported for Encode by the Web Browser. This list of codecs is supported for capture on a basic browser installation.
	 */
	AspectPropertyValue<List<String>> getBrowserAudioCodecsEncode();
	/**
	 * Indicates the age in months of the browser since the BrowserDiscontinuedYear and BrowserDiscontinuedMonth.
	 */
	AspectPropertyValue<Integer> getBrowserDiscontinuedAge();
	/**
	 * The month in which further development of the browser version is stopped by the browser vendor. This occurs when a new stable version of the browser is released.
	 */
	AspectPropertyValue<String> getBrowserDiscontinuedMonth();
	/**
	 * The year in which further development of the browser version is stopped by the browser vendor. This occurs when a new stable version of the browser is released.
	 */
	AspectPropertyValue<Integer> getBrowserDiscontinuedYear();
	/**
	 * Indicates the name of the browser without the default OS or layout engine.
	 */
	AspectPropertyValue<String> getBrowserFamily();
	/**
	 * A list of logos associated with the Browser. The string contains the caption, followed by the full image URL separated with a tab character.
	 */
	AspectPropertyValue<List<String>> getBrowserLogos();
	/**
	 * Indicates the name of the browser. Many mobile browsers, by default, come with an operating system (OS). Unless specifically named, these browsers are named after the accompanying OS and/or the layout engine. 
	 */
	AspectPropertyValue<String> getBrowserName();
	/**
	 * Indicates the age in months of the browser since the BrowserPreviewYear and BrowserPreviewMonth.
	 */
	AspectPropertyValue<Integer> getBrowserPreviewAge();
	/**
	 * The month in which the browser version is originally released as a Beta/Developer version by the browser vendor. This is before it is officially released as a stable version, to ensure wider testing by the community can take place.
	 */
	AspectPropertyValue<String> getBrowserPreviewMonth();
	/**
	 * The year in which the browser version is originally released as a Beta/Developer version by the browser vendor. This is before it is officially released as a stable version, to ensure wider testing by the community can take place.
	 */
	AspectPropertyValue<Integer> getBrowserPreviewYear();
	/**
	 * Indicates the source from which browser properties have been validated. Primary browser data are retrieved from the internal test and populated manually, then they might be validated against an external source such as Caniuse or RingMark. 
	 */
	AspectPropertyValue<String> getBrowserPropertySource();
	/**
	 * A measure of the popularity of this browser version. All browsers are ordered by the number of events associated with that browser that occurred in the sampling period. The browser with the most events is ranked 1, the second 2 and so on.
	 */
	AspectPropertyValue<Integer> getBrowserRank();
	/**
	 * Indicates the age in months of the browser since the BrowserReleaseYear and BrowserReleaseMonth.
	 */
	AspectPropertyValue<Integer> getBrowserReleaseAge();
	/**
	 * The month in which the browser version is officially released to users by the browser vendor. This version is called the stable version as any bugs or difficulties highlighted in the Beta/Developer Version will have been fixed for this release.
	 */
	AspectPropertyValue<String> getBrowserReleaseMonth();
	/**
	 * The year in which the browser version is officially released to users by the browser vendor. This version is called the stable version as any bugs or difficulties highlighted in the Beta/Developer Version will have been fixed for this release.
	 */
	AspectPropertyValue<Integer> getBrowserReleaseYear();
	/**
	 * Name of the underlying browser source project.
	 */
	AspectPropertyValue<String> getBrowserSourceProject();
	/**
	 * Indicates the version or subversion of the underlying browser source project.
	 */
	AspectPropertyValue<String> getBrowserSourceProjectVersion();
	/**
	 * Indicates if the browser supports the experimental Privacy Sandbox API proposals from Google.
	 */
	AspectPropertyValue<String> getBrowserSupportsPrivacySandbox();
	/**
	 * Indicates the name of the company which created the browser.
	 */
	AspectPropertyValue<String> getBrowserVendor();
	/**
	 * Indicates the version or subversion of the browser.
	 */
	AspectPropertyValue<String> getBrowserVersion();
	/**
	 * Refers to the list of video codecs in specific formats supported for Decode by the Web Browser. This list of codecs is supported for playback on a basic browser installation.
	 */
	AspectPropertyValue<List<String>> getBrowserVideoCodecsDecode();
	/**
	 * Refers to the list of video codecs in specific formats supported for Encode by the Web Browser. This list of codecs is supported for capture on a basic browser installation.
	 */
	AspectPropertyValue<List<String>> getBrowserVideoCodecsEncode();
	/**
	 * Indicates the list of camera types the device has. If the device has a rotating camera, this property refers to both front and back facing cameras.
	 */
	AspectPropertyValue<List<String>> getCameraTypes();
	/**
	 * Indicates if the browser supports the canvas element, useful for drawing graphics via scripting (usually JavaScript).
	 */
	AspectPropertyValue<Boolean> getCanvas();
	/**
	 * Stands for Composite Capability/Preference Profiles.  Refers to the list of MIME types supported by the operating system. The list does not include MIME types that are only enabled through the use of 3rd party applications.
	 */
	AspectPropertyValue<List<String>> getCcppAccept();
	/**
	 * Indicates which version of the Connected Limited Device Configuration the device supports for use with Java ME.
	 */
	AspectPropertyValue<Double> getCLDC();
	/**
	 * Indicates the contrast ratio of the device.
	 */
	AspectPropertyValue<String> getContrastRatio();
	/**
	 * Indicates if the browser supports http Cookies. However, the user may have disabled Cookies in their own configuration. Where data cannot be validated, it is assumed that the browser supports cookies.
	 */
	AspectPropertyValue<Boolean> getCookiesCapable();
	/**
	 * Indicates the official name of the CPU within the SoC, e.g. ARM Cortex A9 or Krait (Qualcomm).
	 */
	AspectPropertyValue<String> getCPU();
	/**
	 * Indicates the number of physical CPU cores the device has.
	 */
	AspectPropertyValue<Integer> getCPUCores();
	/**
	 * Indicates the Semiconductor Company that designed the CPU.
	 */
	AspectPropertyValue<String> getCPUDesigner();
	/**
	 * Indicates the maximum frequency of the CPU of the device in gigahertz (GHz).
	 */
	AspectPropertyValue<Double> getCPUMaximumFrequency();
	/**
	 * Indicates the crawler name when applicable. Returns NotCrawler when the device is not a crawler.
	 */
	AspectPropertyValue<String> getCrawlerName();
	/**
	 * Indicates if the browser supports CSS3 background properties (such as background-image, background-color, etc.) that allow styling of the border and the background of an object, and create a shadow effect.
	 */
	AspectPropertyValue<Boolean> getCssBackground();
	/**
	 * Indicates if the browser supports border images, allowing decoration of the border around an object.
	 */
	AspectPropertyValue<Boolean> getCssBorderImage();
	/**
	 * Indicates if the browser can draw CSS images into a Canvas.
	 */
	AspectPropertyValue<Boolean> getCssCanvas();
	/**
	 * Indicates if the browser supports CSS3 Color, allowing author control of the foreground colour and opacity of an element.
	 */
	AspectPropertyValue<Boolean> getCssColor();
	/**
	 * Indicates if the browser supports CSS3 columns for setting column- width and column-count.
	 */
	AspectPropertyValue<Boolean> getCssColumn();
	/**
	 * Indicates if the browser supports flexbox, allowing the automatic reordering of elements on the page when accessed from devices with different screen sizes.
	 */
	AspectPropertyValue<Boolean> getCssFlexbox();
	/**
	 * Indicates if the browser supports CSS3 fonts, including non-standard fonts, e.g. @font-face.
	 */
	AspectPropertyValue<Boolean> getCssFont();
	/**
	 * Indicates if the browser supports all CSS grid properties.
	 */
	AspectPropertyValue<Boolean> getCssGrid();
	/**
	 * Indicates if the browser supports CSS3 images, allowing for fall-back images, gradients and other effects.
	 */
	AspectPropertyValue<Boolean> getCssImages();
	/**
	 * Indicates if the browser supports MediaQueries for dynamic CSS that uses the @media rule.
	 */
	AspectPropertyValue<Boolean> getCssMediaQueries();
	/**
	 * Indicates if the browser supports the CSS 'min-width' and 'max-width' element.
	 */
	AspectPropertyValue<Boolean> getCssMinMax();
	/**
	 * Indicates if the browser supports overflowing of clipped blocks.
	 */
	AspectPropertyValue<Boolean> getCssOverflow();
	/**
	 * Indicates if the browser supports CSS position, allowing for different box placement algorithms, e.g. static, relative, absolute, fixed and initial.
	 */
	AspectPropertyValue<Boolean> getCssPosition();
	/**
	 * Indicates if the browser supports all CSS3 text features including: text-overflow, word-wrap and word-break.
	 */
	AspectPropertyValue<Boolean> getCssText();
	/**
	 * Indicates if the browser supports 2D transformations in CSS3 including rotating, scaling, etc. This property includes support for both transform and transform-origin properties.
	 */
	AspectPropertyValue<Boolean> getCssTransforms();
	/**
	 * Indicates if the browser supports CSS3 transitions elements, used for animating changes to properties.
	 */
	AspectPropertyValue<Boolean> getCssTransitions();
	/**
	 * Indicates if the browser supports CSS UI stylings, including text-overflow, css3-boxsizing and pointer properties.
	 */
	AspectPropertyValue<Boolean> getCssUI();
	/**
	 * Indicates if the browser has the ability to embed custom data attributes on all HTML elements using the 'data-' prefix.
	 */
	AspectPropertyValue<Boolean> getDataSet();
	/**
	 * Indicates if the browser allows encoded data to be contained in a URL.
	 */
	AspectPropertyValue<Boolean> getDataUrl();
	/**
	 * Indicates what certifications apply to this device.
	 */
	AspectPropertyValue<List<String>> getDeviceCertifications();
	/**
	 * Consists of four components separated by a hyphen symbol: Hardware-Platform-Browser-IsCrawler where each Component represents an ID of the corresponding Profile.
	 */
	AspectPropertyValue<String> getDeviceId();
	/**
	 * Indicates if the browser supports DOM events for device orientation, e.g. 'deviceorientation', 'devicemotion' and 'compassneedscalibration'.
	 */
	AspectPropertyValue<Boolean> getDeviceOrientation();
	/**
	 * Indicates the maximum volatile RAM capacity of the device in megabytes (MB). Where a device has different RAM capacity options, the largest option available is returned.
	 */
	AspectPropertyValue<Integer> getDeviceRAM();
	/**
	 * Indicates the volatile RAM capacity options for the device in megabytes (MB). If no variants are found, then the value returned will be the same as "DeviceRAM".
	 */
	AspectPropertyValue<List<String>> getDeviceRAMVariants();
	/**
	 * Indicates the type of the device based on values set in other properties, such as IsMobile, IsTablet, IsSmartphone, IsSmallScreen etc.
	 */
	AspectPropertyValue<String> getDeviceType();
	/**
	 * Used when detection method is not Exact or None. This is an integer value and the larger the value the less confident the detector is in this result.
	 */
	AspectPropertyValue<Integer> getDifference();
	/**
	 * Total difference in character positions where the substrings hashes were found away from where they were expected.
	 */
	AspectPropertyValue<Integer> getDrift();
	/**
	 * Indicates the device's Ingress Protection Rating against dust and water (http://en.wikipedia.org/wiki/IP_Code).
	 */
	AspectPropertyValue<List<String>> getDurability();
	/**
	 * Indicates the dynamic contrast ratio of the device's screen.
	 */
	AspectPropertyValue<String> getDynamicContrastRatio();
	/**
	 * Indicates the annual energy consumption of the device per year in kWh.
	 */
	AspectPropertyValue<Integer> getEnergyConsumptionPerYear();
	/**
	 * Indicates the maximum amount of memory in gigabytes (GB) the expansion slot of the device can support.
	 */
	AspectPropertyValue<Integer> getExpansionSlotMaxSize();
	/**
	 * Indicates the expansion slot type the device can support.
	 */
	AspectPropertyValue<List<String>> getExpansionSlotType();
	/**
	 * Indicates if the browser supports the Fetch API.
	 */
	AspectPropertyValue<Boolean> getFetch();
	/**
	 * Indicates if the browser supports file reading with events to show progress and errors.
	 */
	AspectPropertyValue<Boolean> getFileReader();
	/**
	 * Indicates if the browser allows Blobs to be saved to client machines with events to show progress and errors. The End-User may opt to decline these files.
	 */
	AspectPropertyValue<Boolean> getFileSaver();
	/**
	 * Indicates if the browser allows files to be saved to client machines with events to show progress and errors. The End-User may opt to decline these files.
	 */
	AspectPropertyValue<Boolean> getFileWriter();
	/**
	 * Indicates if the browser supports the 'FormData' object. This property also refers to XMLHttpRequest. If the browser supports 'xhr2', the 'FormData' element will be also supported. 
	 */
	AspectPropertyValue<Boolean> getFormData();
	/**
	 * Indicates the list of frequency bands supported by the device.
	 */
	AspectPropertyValue<List<String>> getFrequencyBands();
	/**
	 * Indicates the resolution of the device's front camera in megapixels. For a device that has a rotating camera the same value is returned for front and back megapixels' properties.
	 */
	AspectPropertyValue<Double> getFrontCameraMegaPixels();
	/**
	 * Indicates if the browser supports requests from a video or canvas element to be displayed in full-screen mode.
	 */
	AspectPropertyValue<Boolean> getFullscreen();
	/**
	 * Indicates if the browser supports a feature to acquire the geographical location. For information on which GeoLoc API the browser supports, refer to another property called JavaScriptPreferredGeoLocApi.
	 */
	AspectPropertyValue<Boolean> getGeoLocation();
	/**
	 * Indicates the official name of the graphical chip within the SoC.
	 */
	AspectPropertyValue<String> getGPU();
	/**
	 * Indicates the Semiconductor Company that designed the GPU.
	 */
	AspectPropertyValue<String> getGPUDesigner();
	/**
	 * Refers to the list of audio codecs supported for decoding by a Chipset. An audio codec is a program used to playback digital audio files. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getHardwareAudioCodecsDecode();
	/**
	 * Refers to the list of audio codecs supported for encoding by a Chipset. An audio codec is a program used to capture digital audio files. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getHardwareAudioCodecsEncode();
	/**
	 * Indicates the carrier when the device is sold by the HardwareVendor on a single carrier or as indicated via device User-Agent.
	 */
	AspectPropertyValue<String> getHardwareCarrier();
	/**
	 * Indicates the name of a group of devices that only differ by model or region but are marketed under the same name, e.g. Galaxy Tab S 10.5.
	 */
	AspectPropertyValue<String> getHardwareFamily();
	/**
	 * A list of images associated with the device. The string contains the caption, followed by the full image URL separated with a tab character.
	 */
	AspectPropertyValue<List<String>> getHardwareImages();
	/**
	 * Indicates the model name or number used primarily by the hardware vendor to identify the device, e.g.SM-T805S. When a model identifier is not available the HardwareName will be used.
	 */
	AspectPropertyValue<String> getHardwareModel();
	/**
	 * Indicates all model numbers used by the hardware vendor to identify the device. This property compliments 'HardwareModel', e.g. Hardware Model Variants A1660 and A1778 correlate to the Hardware Model - iPhone 7.
	 */
	AspectPropertyValue<List<String>> getHardwareModelVariants();
	/**
	 * Indicates the common marketing names associated with the device, e.g. Xperia Z5.
	 */
	AspectPropertyValue<List<String>> getHardwareName();
	/**
	 * Indicates the source of the profile's specifications. This property will return 'Manufacturer' value if the profile data was obtained from the manufacturer of the device or the device itself. This property will return 'Authoritative' value if the profile data was not obtained from the manufacturer or the device itself but other third party sources (this may include retailers, social media, carriers, etc). This property will return 'Legacy' value if the profile data was obtained prior to 51degrees differentiating between Manufacturer and Authoritative. This property will return 'N/A' value if the profile data was not obtained due to unidentifiable User-Agent. The example profiles are: Generic Android Unknown, Unknown Tablet, etc.
	 */
	AspectPropertyValue<String> getHardwareProfileSource();
	/**
	 * A measure of the popularity of this device model. All models are ordered by the number of events associated with that model that occurred in the sampling period. The device with the most events is ranked 1, the second 2 and so on. 
	 */
	AspectPropertyValue<Integer> getHardwareRank();
	/**
	 * Indicates the name of the company that manufactures the device or primarily sells it, e.g. Samsung.
	 */
	AspectPropertyValue<String> getHardwareVendor();
	/**
	 * Refers to the list of video codecs supported for decoding by a Chipset. An video codec is a program used to playback digital video files. The values of this property are the codec's common name. 
	 */
	AspectPropertyValue<List<String>> getHardwareVideoCodecsDecode();
	/**
	 * Refers to the list of video codecs supported for encoding by a Chipset. An video codec is a program used to capture digital video files. The values of this property are the codec's common name. 
	 */
	AspectPropertyValue<List<String>> getHardwareVideoCodecsEncode();
	/**
	 * Indicates if the device has a camera capable of taking 3D images.
	 */
	AspectPropertyValue<Boolean> getHas3DCamera();
	/**
	 * Indicates if the device has a screen capable of displaying 3D images.
	 */
	AspectPropertyValue<Boolean> getHas3DScreen();
	/**
	 * Indicates if the device has a camera.
	 */
	AspectPropertyValue<Boolean> getHasCamera();
	/**
	 * Indicates if the device has a click wheel such as found on Apple iPod devices.
	 */
	AspectPropertyValue<Boolean> getHasClickWheel();
	/**
	 * Indicates if the device has a physical numeric keypad.
	 */
	AspectPropertyValue<Boolean> getHasKeypad();
	/**
	 * Indicates if the device has embedded NFC (Near Field Communication) wireless technology.
	 */
	AspectPropertyValue<Boolean> getHasNFC();
	/**
	 * Indicates if the device has a physical qwerty keyboard.
	 */
	AspectPropertyValue<Boolean> getHasQwertyPad();
	/**
	 * Indicates if the device has a removable battery. This property is not applicable for devices that do not have batteries. Unless otherwise stated this property will return a 'False' value for tablets.
	 */
	AspectPropertyValue<Boolean> getHasRemovableBattery();
	/**
	 * Indicates if the device has a touch screen. This property will return 'False' for a device that does not have an integrated screen.
	 */
	AspectPropertyValue<Boolean> getHasTouchScreen();
	/**
	 * Indicates if the device has a trackpad or trackball. Examples of devices that support this property are the Nexus One and Blackberry Curve.
	 */
	AspectPropertyValue<Boolean> getHasTrackPad();
	/**
	 * Indicates if the device has a virtual qwerty keyboard capability.
	 */
	AspectPropertyValue<Boolean> getHasVirtualQwerty();
	/**
	 * Indicates if the browser stores the session history for a web page that contains the URLs visited by the browser's user.
	 */
	AspectPropertyValue<Boolean> getHistory();
	/**
	 * Indicates if the browser is able to use media inputs, e.g. webcam and microphone, in a script and as an input for forms, e.g. '&lt;input type="file" accept="image/*" id="capture"&gt;' would prompt image- capturing software to open.
	 */
	AspectPropertyValue<Boolean> getHtmlMediaCapture();
	/**
	 * Indicates if the browser supports the new markup in HTML 5 that also refers to 'New Semantic Elements' such as <header>, <nav>, <section>, <aside>,<footer> etc.
	 */
	AspectPropertyValue<Boolean> getHtml5();
	/**
	 * Lists what audio formats, if any, the browser supports using the HTML5 <audio> tag.
	 */
	AspectPropertyValue<List<String>> getHtml5Audio();
	/**
	 * Lists what video formats, if any, the browser supports using the HTLM5 <video> tag.
	 */
	AspectPropertyValue<List<String>> getHtml5Video();
	/**
	 * Refers to the latest version of HyperText Markup Language (HTML) supported by the browser.
	 */
	AspectPropertyValue<Double> getHtmlVersion();
	/**
	 * Indicates if the browser supports HTTP version 2.
	 */
	AspectPropertyValue<Boolean> getHttp2();
	/**
	 * Indicates if the browser supports HTTP Live Streaming, also known as HLS.
	 */
	AspectPropertyValue<String> getHttpLiveStreaming();
	/**
	 * Indicates if the browser supports the 'Iframe' element, used to embed another document within a current HTML document.
	 */
	AspectPropertyValue<Boolean> getIframe();
	/**
	 * Indicates if the browser supports an indexed local database.
	 */
	AspectPropertyValue<Boolean> getIndexedDB();
	/**
	 * Indicates the internal persistent storage (ROM capacity) options the device can be supplied with in gigabytes (GB), including the device's Operating System and bundled applications. This could also be referred to as "Electrically Erasable Programmable Read-Only Memory (EEPROM)" or "Non Volatile Random Access Memory (NVRAM)". If no variants are found, then the value returned will be the same as "MaxInternalStorage".
	 */
	AspectPropertyValue<List<String>> getInternalStorageVariants();
	/**
	 * Indicates if a web page is accessed through a VR headset.
	 */
	AspectPropertyValue<Boolean> getInVRMode();
	/**
	 * Indicates whether the crawler is confirmed by the crawler controller to be used to train artificial intelligence.
	 */
	AspectPropertyValue<String> getIsArtificialIntelligence();
	/**
	 * Indicates if the device is primarily a game console, such as an Xbox or Playstation.
	 */
	AspectPropertyValue<Boolean> getIsConsole();
	/**
	 * Indicates if the source of the web traffic identifies itself as operating without human interaction for the purpose of monitoring the availability or performance of a web site, retrieving a response for inclusion in a search engine or is requesting structured data such as via an API. Such sources are often referred to as crawlers, bots, robots, spiders, probes, monitors or HTTP services among other terms. Where the source pretends to be a device operating with human interaction, such as a smartphone or tablet, this property will return, 'False'.
	 */
	AspectPropertyValue<Boolean> getIsCrawler();
	/**
	 * Indicates if the browser may be optimised for low bandwidth. A true value indicates the browser supports a feature that can improve performance on low bandwidth connections, either via the removal of elements, features, a proxy or other methods.
	 */
	AspectPropertyValue<Boolean> getIsDataMinimising();
	/**
	 * Indicates if the application is an email browser (Outlook, Gmail, YahooMail, etc.) that is primarily used to access and manage emails (usually from mobile devices).
	 */
	AspectPropertyValue<Boolean> getIsEmailBrowser();
	/**
	 * Indicates if the mobile device accessing a web page emulates a desktop computer. This property is not applicable for desktops, media hubs, TVs and consoles.
	 */
	AspectPropertyValue<Boolean> getIsEmulatingDesktop();
	/**
	 * Indicates a browser that does not correctly identify the physical hardware device and instead reports an emulated device.
	 */
	AspectPropertyValue<Boolean> getIsEmulatingDevice();
	/**
	 * Indicates if the device is primarily advertised as an e-reader. If the device type is EReader then the device is not classified as a tablet.
	 */
	AspectPropertyValue<Boolean> getIsEReader();
	/**
	 * Indicates a profile which contains more than a single hardware device. When this is true all returned properties represent the default value or lowest given specification of all grouped devices. E.g. the profile representing unknown Windows 10 tablets will return true. Apple devices detected through JavascriptHardwareProfile that do not uniquely identify a device will also return true, and HardwareModelVariants will return a list of model numbers associated with that device group.
	 */
	AspectPropertyValue<Boolean> getIsHardwareGroup();
	/**
	 * Indicates if the device is a media hub or set top box that requires an external display(s).
	 */
	AspectPropertyValue<Boolean> getIsMediaHub();
	/**
	 * Indicates if the device's primary data connection is wireless and the device is designed to operate mostly by battery power (e.g. mobile phone, smartphone or tablet). This property does not indicate if the device is a mobile phone or not. Laptops are not classified as mobile devices under this definition and so 'IsMobile' will be 'False'.
	 */
	AspectPropertyValue<Boolean> getIsMobile();
	/**
	 * Indicates whether the device screen is foldable or not. If the device does not have a screen or the screen is not foldable, 'False' is returned.
	 */
	AspectPropertyValue<Boolean> getIsScreenFoldable();
	/**
	 * Indicates if the device is a mobile with a screen size less than 2.5 inches even where the device is marketed as a Smartphone.
	 */
	AspectPropertyValue<Boolean> getIsSmallScreen();
	/**
	 * Indicates whether the device can make and receive phone calls, has a screen size greater than or equal to 2.5 inches, runs a modern operating system (Android, iOS, Windows Phone, BlackBerry etc.), is not designed to be a wearable technology and is marketed by the vendor as a Smartphone.
	 */
	AspectPropertyValue<Boolean> getIsSmartPhone();
	/**
	 * Indicates if the device is a web enabled computerised wristwatch with other capabilities beyond timekeeping, such as push notifications. It runs on a Smart Operating System i.e. Android, WatchOS, Tizen, Ubuntu Touch and is designed to be wearable technology.
	 */
	AspectPropertyValue<Boolean> getIsSmartWatch();
	/**
	 * Indicates if the device is primarily marketed as a tablet or phablet and has a screen size equal to or greater than 7 inches.
	 */
	AspectPropertyValue<Boolean> getIsTablet();
	/**
	 * Indicates if the device is a TV running on a smart operating system e.g. Android.
	 */
	AspectPropertyValue<Boolean> getIsTv();
	/**
	 * Indicates if a web page is accessed from an application whose main function is not browsing the World Wide Web or managing emails, e.g. the Facebook App. The application must be downloaded and installed onto the device from an app marketplace such as Apple's App Store or the Google Play Store, or via a third party as an .apk file or similar. This property will return a 'False' value for mobile browsers such as Chrome Mobile or email browsers (such as Hotmail).
	 */
	AspectPropertyValue<Boolean> getIsWebApp();
	/**
	 * The number of iterations carried out in order to find a match. This is the number of nodes in the graph which have been visited.
	 */
	AspectPropertyValue<Integer> getIterations();
	/**
	 * Indicates if the browser supports JavaScript.
	 */
	AspectPropertyValue<Boolean> getJavascript();
	/**
	 * JavaScript that checks for browser specific features and overrides the ProfileID.
	 */
	AspectPropertyValue<JavaScript> getJavaScriptBrowserOverride();
	/**
	 * Indicates if the browser supports the JavaScript that can manipulate CSS on the browser's web page.
	 */
	AspectPropertyValue<Boolean> getJavascriptCanManipulateCSS();
	/**
	 * Indicates if the browser supports the JavaScript that can manipulate the Document Object Model on the browser's web page.
	 */
	AspectPropertyValue<Boolean> getJavascriptCanManipulateDOM();
	/**
	 * Indicates if the browser supports JavaScript that is able to access HTML elements from their ID using the getElementById method.
	 */
	AspectPropertyValue<Boolean> getJavascriptGetElementById();
	/**
	 * Contains Javascript to get high entropy values.
	 */
	AspectPropertyValue<JavaScript> getJavascriptGetHighEntropyValues();
	/**
	 * JavaScript that can override the profile found by the server using information on the client device. This property is applicable for Apple devices which do not provide information about the model in the User-Agent string.
	 */
	AspectPropertyValue<JavaScript> getJavascriptHardwareProfile();
	/**
	 * Refers to the JavaScript snippet used to optimise images.
	 */
	AspectPropertyValue<JavaScript> getJavascriptImageOptimiser();
	/**
	 * Indicates which GeoLoc API JavaScript the browser supports. If a browser supports a feature to acquire the user's geographical location, another property called 'GeoLocation' will be set to True.
	 */
	AspectPropertyValue<String> getJavascriptPreferredGeoLocApi();
	/**
	 * Indicates if the browser allows registration of event listeners on event targets by using the addEventListener() method.
	 */
	AspectPropertyValue<Boolean> getJavascriptSupportsEventListener();
	/**
	 * Indicates if the browser supports the JavaScript events 'onload', 'onclick' and 'onselect'. 
	 */
	AspectPropertyValue<Boolean> getJavascriptSupportsEvents();
	/**
	 * Indicates if the browser supports the JavaScript that is able to insert HTML into a DIV tag.
	 */
	AspectPropertyValue<Boolean> getJavascriptSupportsInnerHtml();
	/**
	 * Indicates which JavaScript version the browser uses. The number refers to JavaScript versioning, not ECMAscript or Jscript. If the browser doesn't support JavaScript then 'NotSupported' value is returned.
	 */
	AspectPropertyValue<String> getJavascriptVersion();
	/**
	 * Indicates the browser supports JPEG 2000 image format.
	 */
	AspectPropertyValue<Boolean> getJpeg2000();
	/**
	 * Refers to the grade of the level the device has with the jQuery Mobile Framework, as posted by jQuery.
	 */
	AspectPropertyValue<String> getjQueryMobileSupport();
	/**
	 * Indicates if the browser supports the 'JSON' object. This property may need a vendor prefix, e.g. webkit, moz, etc.
	 */
	AspectPropertyValue<Boolean> getJson();
	/**
	 * Refers to the name of the embedded technology the browser uses to display formatted content on the screen.
	 */
	AspectPropertyValue<String> getLayoutEngine();
	/**
	 * Indicates if the browser supports the CSS-mask element that allows users to alter the visibility of an item by either partially or fully hiding the item.
	 */
	AspectPropertyValue<Boolean> getMasking();
	/**
	 * Indicates the number of hash nodes matched within the evidence.
	 */
	AspectPropertyValue<Integer> getMatchedNodes();
	/**
	 * Indicates the maximum amount of internal persistent storage (ROM capacity) with which the device is supplied in gigabytes (GB), including the space used by the device's Operating System and bundled applications. This could also be referred to as "Electrically Erasable Programmable Read-Only Memory (EEPROM)" or "Non Volatile Random Access Memory (NVRAM)". Where a device has different internal storage options, the largest option available is returned.
	 */
	AspectPropertyValue<Double> getMaxInternalStorage();
	/**
	 * Indicates the maximum number of "Universal Integrated Circuit Cards (UICC - more commonly known as, SIM)" the device can support including both removable and embedded. If the device doesn't support any UICC then a value of '0' is returned.
	 */
	AspectPropertyValue<Integer> getMaxNumberOfSIMCards();
	/**
	 * Indicates the maximum standby time of the device in hours. This property is not applicable for a device without a battery.
	 */
	AspectPropertyValue<Integer> getMaxStandbyTime();
	/**
	 * Indicates the maximum talk time of the device in minutes. This property is not applicable for a device that does not have a battery or support phone calls.
	 */
	AspectPropertyValue<Integer> getMaxTalkTime();
	/**
	 * Indicates the maximum general usage time of the device in minutes. This property is not applicable for a device without a battery.
	 */
	AspectPropertyValue<Integer> getMaxUsageTime();
	/**
	 * Indicates if the browser supports a meter element that represents a scalar measurement within a known range or fractional value. This property does not indicate whether the browser supports the progress bar indication. For this purpose, the progress property should be used.
	 */
	AspectPropertyValue<Boolean> getMeter();
	/**
	 * The method used to determine the match result.
	 */
	AspectPropertyValue<String> getMethod();
	/**
	 * Indicates which version of Mobile Information Device Profile the device supports, used with Java ME and CLDC.
	 */
	AspectPropertyValue<Double> getMIDP();
	/**
	 * Refers to the 'Retail Branding' value returned for Android Google Play native applications, when the android.os.Build.BRAND javascript is used to display the class. This property is not applicable for hardware running on operating systems other than Android.
	 */
	AspectPropertyValue<List<String>> getNativeBrand();
	/**
	 * Refers to the 'Device' value returned for Android Google Play native applications, when the android.os.Build.DEVICE javascript is used to display the class. This property is not applicable for hardware running on operating systems other than Android.
	 */
	AspectPropertyValue<List<String>> getNativeDevice();
	/**
	 * Refers to the 'Model' value returned for Android Google Play native applications, when the android.os.Build.MODEL javascript is used to display the class. For Apple devices this property refers to the device identifier which appears in the native application from the developer usage log, for example 'iPad5,4'.
	 */
	AspectPropertyValue<List<String>> getNativeModel();
	/**
	 * NativeName Refers to the 'Marketing Name' value that a device is registered with on the Google Play service. This property is not applicable for hardware running on operating systems other than Android.
	 */
	AspectPropertyValue<List<String>> getNativeName();
	/**
	 * Indicates the name of the mobile operating system (iOS, Android) for which an application program has been developed to be used by a device.
	 */
	AspectPropertyValue<String> getNativePlatform();
	/**
	 * Indicates the number of screens the device has. This property is not applicable for a device that does not have a screen.
	 */
	AspectPropertyValue<Integer> getNumberOfScreens();
	/**
	 * Indicates the name of the company that manufactures the device.
	 */
	AspectPropertyValue<String> getOEM();
	/**
	 * Indicates the power consumption of the device while switched on.
	 */
	AspectPropertyValue<Integer> getOnPowerConsumption();
	/**
	 * The ratio of the resolution in physical pixels to the resolution in CSS pixels. This is approximated by screen resolution and screen size when the value is not known.
	 */
	AspectPropertyValue<Double> getPixelRatio();
	/**
	 * JavaScript that can override the property value found by the server using information on the client device. This property is applicable for browsers that support pixel ratio cookie.
	 */
	AspectPropertyValue<JavaScript> getPixelRatioJavascript();
	/**
	 * Indicates the age in months of the operating system since the PlatformReleaseYear and PlatformReleaseMonth.
	 */
	AspectPropertyValue<Integer> getPlatformDiscontinuedAge();
	/**
	 * The month in which further development for the platform version is stopped by the platform vendor. This occurs when a new stable version of the platform is released.
	 */
	AspectPropertyValue<String> getPlatformDiscontinuedMonth();
	/**
	 * The year in which further development for the platform version is stopped by the platform vendor. This occurs when a new stable version of the platform is released.
	 */
	AspectPropertyValue<Integer> getPlatformDiscontinuedYear();
	/**
	 * A list of logos associated with the Software. The string contains the caption, followed by the full image URL separated with a tab character.
	 */
	AspectPropertyValue<List<String>> getPlatformLogos();
	/**
	 * Indicates the name of the operating system the device is using.
	 */
	AspectPropertyValue<String> getPlatformName();
	/**
	 * Indicates the age in months of the operating system since the PlatformPreviewYear and PlatformPreviewMonth.
	 */
	AspectPropertyValue<Integer> getPlatformPreviewAge();
	/**
	 * The month in which the platform version was originally released as a Beta/Developer version by the platform vendor. This is before it is officially released as a stable version, to ensure wider testing by the community can take place.
	 */
	AspectPropertyValue<String> getPlatformPreviewMonth();
	/**
	 * The year in which the platform version was originally released as a Beta/Developer version by the platform vendor. This is before it is officially released as a stable version, to ensure wider testing by the community can take place.
	 */
	AspectPropertyValue<Integer> getPlatformPreviewYear();
	/**
	 * A measure of the popularity of this software platform (i.e. OS and version). All platforms are ordered by the number of events associated with that platform that occurred in the sampling period. The platform with the most events is ranked 1, the second 2 and so on.
	 */
	AspectPropertyValue<Integer> getPlatformRank();
	/**
	 * Indicates the age in months of the operating system since the PlatformReleaseYear and PlatformReleaseMonth.
	 */
	AspectPropertyValue<Integer> getPlatformReleaseAge();
	/**
	 * The month in which the platform version was officially released to users by the platform vendor. This version is called the stable version as any bugs or difficulties highlighted in the Beta/Developer version will have been fixed for this release.
	 */
	AspectPropertyValue<String> getPlatformReleaseMonth();
	/**
	 * The year in which the platform version was officially released to users by the platform vendor. This version is called the stable version as any bugs or difficulties highlighted in the Beta/Developer Version will have been fixed for this release.
	 */
	AspectPropertyValue<Integer> getPlatformReleaseYear();
	/**
	 * Indicates the name of the company that developed the operating system.
	 */
	AspectPropertyValue<String> getPlatformVendor();
	/**
	 * Indicates the version or subversion of the software platform.
	 */
	AspectPropertyValue<String> getPlatformVersion();
	/**
	 * Indicates if the browser supports messages between different documents.
	 */
	AspectPropertyValue<Boolean> getPostMessage();
	/**
	 * Indicates if the browser can prefetch resources without executing them.
	 */
	AspectPropertyValue<Boolean> getPreload();
	/**
	 * Indicates a price range describing the recommended retail price of the device at the date of release, inclusive of tax (where applicable).  Prices are in United States Dollars (USD); if the price is not originally in USD it will be converted to USD using the relevant exchange rate at the time of launch. Prices are for the SIM-free version of the device (if applicable). In cases where there are several versions of the same model of the device, the price will reflect the device that was used to populate the specifications.
	 */
	AspectPropertyValue<String> getPriceBand();
	/**
	 * Indicates if the browser supports progress reports, such as with HTTP requests. The progress element can be used to display the progress of the task. This property doesn't represent a scalar measurement. If the browser supports a gauge, the meter property should be used.
	 */
	AspectPropertyValue<Boolean> getProgress();
	/**
	 * Indicates the level of support for the Promise object. The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value.
	 */
	AspectPropertyValue<String> getPromise();
	/**
	 * Indicates if the browser supports simple dialogues (window.alert, window.confirm and window.prompt).
	 */
	AspectPropertyValue<Boolean> getPrompts();
	/**
	 * Refers to the experimental Privacy Sandbox Protected Audience API proposal from Google. Indicates whether the API caller can register an "AdInterestGroup" and checks whether the website has not blocked the Protected Audience API using a Permissions Policy. Please be aware we have observed latency issues when interacting with the API.
	 */
	AspectPropertyValue<String> getProtectedAudienceAPIEnabled();
	/**
	 * JavaScript that overrides the property value for the ProtectedAudienceAPIEnabled property.
	 */
	AspectPropertyValue<JavaScript> getProtectedAudienceAPIEnabledJavaScript();
	/**
	 * Indicates the maximum number of frames per second of the output image of the device in Hertz.
	 */
	AspectPropertyValue<Integer> getRefreshRate();
	/**
	 * Indicates the age in months of the device since the ReleaseYear and ReleaseMonth.
	 */
	AspectPropertyValue<Integer> getReleaseAge();
	/**
	 * Indicates the month in which the device was released or the month in which the device was first seen by 51Degrees (if the release date cannot be identified).
	 */
	AspectPropertyValue<String> getReleaseMonth();
	/**
	 * Indicates the year in which the device was released or the year in which the device was first seen by 51Degrees (if the release date cannot be identified).
	 */
	AspectPropertyValue<Integer> getReleaseYear();
	/**
	 * Indicates  the device's supported satellite navigation types.
	 */
	AspectPropertyValue<List<String>> getSatelliteNavigationTypes();
	/**
	 * Indicates the diagonal size of the device's screen in inches, to a maximum of two decimal points. Where screens have curved corners, the actual viewable area may be less.
	 */
	AspectPropertyValue<Double> getScreenInchesDiagonal();
	/**
	 * Indicates the diagonal size of the device's screen in inches rounded to the nearest whole number. This property will return the value 'Unknown' for desktop or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Integer> getScreenInchesDiagonalRounded();
	/**
	 * Refers to the height of the device's screen in inches. This property will return 'Unknown' for desktops or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Double> getScreenInchesHeight();
	/**
	 * Indicates the area of the device's screen in square inches rounded to the nearest whole number. This property will return the value 'Unknown' for desktop or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Integer> getScreenInchesSquare();
	/**
	 * Refers to the width of the device's screen in inches. This property will return the value 'Unknown' for desktop or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Double> getScreenInchesWidth();
	/**
	 * Refers to the diagonal size of the screen of the device in millimetres. This property will return 'Unknown' for desktops or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Double> getScreenMMDiagonal();
	/**
	 * Indicate the diagonal size of the device's screen in millimetres rounded to the nearest whole number. This property will return the value 'Unknown' for desktop or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Integer> getScreenMMDiagonalRounded();
	/**
	 * Refers to the screen height of the device in millimetres. This property will return 'Unknown' for desktops or for devices which do not have an integrated screen. For devices such as tablets or TV which are predominantly used in landscape mode, the screen height will be the smaller value compared to the screen width.
	 */
	AspectPropertyValue<Double> getScreenMMHeight();
	/**
	 * Indicates the area of the device's screen in square millimetres rounded to the nearest whole number. This property will return the value  'Unknown' for desktop or for devices which do not have an integrated screen.
	 */
	AspectPropertyValue<Integer> getScreenMMSquare();
	/**
	 * Refers to the screen width of the device in millimetres. This property will return 'Unknown' for desktops or for devices which do not have an integrated screen. For devices such as tablets or TV which are predominantly used in landscape mode, the screen height will be the smaller value compared to the screen width.
	 */
	AspectPropertyValue<Double> getScreenMMWidth();
	/**
	 * Indicates the height of the device's screen in pixels.This property is not applicable for a device that does not have a screen. For devices such as tablets or TV which are predominantly used in landscape mode, the pixel height will be the smaller value compared to the pixel width.
	 */
	AspectPropertyValue<Integer> getScreenPixelsHeight();
	/**
	 * JavaScript that can override the property value found by the server using information on the client device. This property is applicable for browsers that support screen pixels height cookie.
	 */
	AspectPropertyValue<JavaScript> getScreenPixelsHeightJavaScript();
	/**
	 * Indicates the height of the device's screen in physical pixels. This property is not applicable for a device that does not have a screen. For devices such as tablets or TV which are predominantly used in landscape mode, the pixel height will be the smaller value compared to the pixel width. 
	 */
	AspectPropertyValue<Integer> getScreenPixelsPhysicalHeight();
	/**
	 * Indicates the width of the device's screen in physical pixels. This property is not applicable for a device that does not have a screen. For devices such as tablets or TV which are predominantly used in landscape mode, the pixel width will be the larger value compared to the pixel height.
	 */
	AspectPropertyValue<Integer> getScreenPixelsPhysicalWidth();
	/**
	 * Indicates the width of the device's screen in pixels. This property is not applicable for a device that does not have a screen. For devices such as tablets or TV which are predominantly used in landscape mode, the pixel width will be the larger value compared to the pixel height.
	 */
	AspectPropertyValue<Integer> getScreenPixelsWidth();
	/**
	 * JavaScript that can override the property value found by the server using information on the client device. This property is applicable for browsers that support screen pixels width cookie. 
	 */
	AspectPropertyValue<JavaScript> getScreenPixelsWidthJavaScript();
	/**
	 * Indicates the screen type of the device. This property is not applicable for a device that does not have an integrated screen, e.g. a media hub. If the device manufacturer or vendor does not specify what the screen type of the device is then it is assumed the device has an LCD screen.
	 */
	AspectPropertyValue<String> getScreenType();
	/**
	 * Indicates the resolution of the device's second back camera in megapixels.
	 */
	AspectPropertyValue<Double> getSecondBackCameraMegaPixels();
	/**
	 * Indicates the resolution of the device's second front camera in megapixels.
	 */
	AspectPropertyValue<Double> getSecondFrontCameraMegaPixels();
	/**
	 * Indicates the diagonal size of the device's second screen in inches. This property is not applicable for a device that does not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenInchesDiagonal();
	/**
	 * Indicates the diagonal size of the device's second screen in inches rounded to the nearest whole number. This property will return the value 'N/A' for desktop or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenInchesDiagonalRounded();
	/**
	 * Refers to the height of the device's second screen in inches. This property will return 'N/A' for desktops or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenInchesHeight();
	/**
	 * Indicates the area of the device's second screen in square inches rounded to the nearest whole number. This property will return the value 'N/A' for desktop or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenInchesSquare();
	/**
	 * Refers to the width of the device's second screen in inches. This property will return the value 'N/A' for desktop or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenInchesWidth();
	/**
	 * Refers to the diagonal size of the second screen of the device in millimetres. This property will return 'N/A' for desktops or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenMMDiagonal();
	/**
	 * Indicate the diagonal size of the device's second screen in millimetres rounded to the nearest whole number. This property will return the value 'N/A' for desktop or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenMMDiagonalRounded();
	/**
	 * Refers to the second screen height of the device in millimetres. This property will return 'N/A' for desktops or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenMMHeight();
	/**
	 * Indicates the area of the device's second screen in square millimetres rounded to the nearest whole number. This property will return the value  'N/A' for desktop or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenMMSquare();
	/**
	 * Refers to the second screen width of the device in millimetres. This property will return 'N/A' for desktops or for devices which do not have a second screen.
	 */
	AspectPropertyValue<Double> getSecondScreenMMWidth();
	/**
	 * Indicates the height of the device's second screen in pixels. This property is not applicable for a device that does not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenPixelsHeight();
	/**
	 * Indicates the width of the device's second screen in pixels. This property is not applicable for a device that does not have a second screen.
	 */
	AspectPropertyValue<Integer> getSecondScreenPixelsWidth();
	/**
	 * Indicates if the browser supports the querySelector() method that returns the first element matching a specified CSS selector(s) in the document.
	 */
	AspectPropertyValue<Boolean> getSelector();
	/**
	 * Contains the Accept-CH HTTP header values to add to the HTTP response for the browser component. UACH values Sec-CH-UA, and Sec-CH-UA-Full-Version are relevant. The default value is Unknown if the browser does not fully support UACH.
	 */
	AspectPropertyValue<String> getSetHeaderBrowserAcceptCH();
	/**
	 * Contains the Accept-CH HTTP header values to add to the HTTP response for the hardware component. UACH values Sec-CH-UA-Model, and Sec-CH-UA-Mobile are relevant. The default value is Unknown if the browser does not fully support UACH.
	 */
	AspectPropertyValue<String> getSetHeaderHardwareAcceptCH();
	/**
	 * Contains the Accept-CH HTTP header values to add to the HTTP response for the platform component. UACH values Sec-CH-UA-Platform, and Sec-CH-UA-Platform-Version are relevant. The default value is Unknown if the browser does not fully support UACH.
	 */
	AspectPropertyValue<String> getSetHeaderPlatformAcceptCH();
	/**
	 * Refers to the experimental Privacy Sandbox Shared Storage API proposal from Google. Indicates whether the API caller can access "Shared Storage" and checks whether the website has not blocked the Shared Storage API using a Permissions Policy.
	 */
	AspectPropertyValue<String> getSharedStorageAPIEnabled();
	/**
	 * JavaScript that overrides the property value for the SharedStorageAPIEnabled property.
	 */
	AspectPropertyValue<JavaScript> getSharedStorageAPIEnabledJavaScript();
	/**
	 * Indicates the primary marketing name of the System on Chip (chipset) which includes the CPU, GPU and modem. e.g. Snapdragon S4
	 */
	AspectPropertyValue<String> getSoC();
	/**
	 * Indicates the Semiconductor Company that designed the System on Chip (chipset) e.g. Qualcomm, Intel or Mediatek.
	 */
	AspectPropertyValue<String> getSoCDesigner();
	/**
	 * Indicates the official model of the System on Chip (chipset) e.g. MSM8625, MT8312.
	 */
	AspectPropertyValue<String> getSoCModel();
	/**
	 * Refers to the list of audio codecs supported by an operating system. This list of codecs is supported for playback on a  basic software installation. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getSoftwareAudioCodecsDecode();
	/**
	 * Refers to the list of audio codecs supported by an operating system. This list of codecs is supported for capture on a basic software installation. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getSoftwareAudioCodecsEncode();
	/**
	 * Refers to the list of video codecs supported by an operating system. This list of codecs is supported for playback on a  basic software installation. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getSoftwareVideoCodecsDecode();
	/**
	 * Refers to the list of video codecs supported by an operating system. This list of codecs is supported for capture on a basic software installation. The values of this property are the codec's common name.
	 */
	AspectPropertyValue<List<String>> getSoftwareVideoCodecsEncode();
	/**
	 * The Specific Absorbtion Rate (SAR) is a measure of the rate at which energy is absorbed by the human body when exposed by a radio frequency electromagnetic field. This property contains values in Watts per Kilogram (W/kg) in accordance with the European Committee for Electrotechnical Standardization (CENELEC).
	 */
	AspectPropertyValue<Double> getSpecificAbsorbtionRateEU();
	/**
	 * The Specific Absorbtion Rate (SAR) is a measure of the rate at which energy is absorbed by the human body when exposed by a radio frequency electromagnetic field. This property contains values in Watts per Kilogram (W/kg) in accordance with the Federal Communications Commission (FCC).
	 */
	AspectPropertyValue<Double> getSpecificAbsorbtionRateUS();
	/**
	 * A list of MIME types the device can stream. The list does not include MIME types that are only supported through the use of 3rd party applications.
	 */
	AspectPropertyValue<List<String>> getStreamingAccept();
	/**
	 * Refers to the suggested optimum height of a button in millimetres. Ensures the button is touchable on a touch screen and not too large on a non-touch screen. Assumes the actual device DPI (Dots Per Inch) is being used. 
	 */
	AspectPropertyValue<Double> getSuggestedImageButtonHeightMms();
	/**
	 * Refers to the suggested optimum height of a button in millimetres. Ensures the button is touchable on a touch screen and not too large on a non-touch screen. 
	 */
	AspectPropertyValue<Double> getSuggestedImageButtonHeightPixels();
	/**
	 * Refers to the suggested optimum height of a hyperlink in pixels. Ensures the link is touchable on a touch screen and not too large on a non-touch screen. Assumes the actual device DPI is being used.
	 */
	AspectPropertyValue<Double> getSuggestedLinkSizePixels();
	/**
	 * Refers to the suggested optimum height of a hyperlink in points. Ensures the link is touchable on a touch screen and not too large on a non-touch screen. 
	 */
	AspectPropertyValue<Double> getSuggestedLinkSizePoints();
	/**
	 * Indicates the list of wireless data technologies supported by the device, including Bluetooth and Wi-Fi. For example, 4G cellular network technologies includes 'LTE' (Long Term Evolution), and 5G technologies includes 'NR' (New Radio). If the device supports phone calls, the SMS value is also returned.
	 */
	AspectPropertyValue<List<String>> getSupportedBearers();
	/**
	 * Indicates the highest version of Bluetooth the device supports.
	 */
	AspectPropertyValue<Double> getSupportedBluetooth();
	/**
	 * Indicates the Bluetooth profiles the device supports.
	 */
	AspectPropertyValue<List<String>> getSupportedBluetoothProfiles();
	/**
	 * This Property is no longer being supported. Please see Properties, SupportedBluetooth and SupportedBluetoothProfiles for the relevant data.
	 */
	AspectPropertyValue<String> getSupportedBluetoothVersion();
	/**
	 * Indicates the list of features the device's camera supports.
	 */
	AspectPropertyValue<List<String>> getSupportedCameraFeatures();
	/**
	 * Indicates the list of charger types supported by the device. For devices that operate via mains power only, e.g. TVs, MediaHubs (which technically aren't being charged) this property is not applicable.
	 */
	AspectPropertyValue<List<String>> getSupportedChargerTypes();
	/**
	 * Indicates the list of input and output communications the device can support, for example 3.5mm jack, micro-USB etc.
	 */
	AspectPropertyValue<List<String>> getSupportedIO();
	/**
	 * Indicates the list of sensors supported by the device. This property may be not applicable for devices without sensors, such as most feature phones and media hubs.
	 */
	AspectPropertyValue<List<String>> getSupportedSensorTypes();
	/**
	 * Indicates whether the "Universal Integrated Circuit Card (UICC - more commonly known as, SIM)" is removable or embedded. If removable, the form factor of the UICC is returned.
	 */
	AspectPropertyValue<List<String>> getSupportedSIMCardTypes();
	/**
	 * Indicates if the device supports 24p; a video format that operates at 24 frames per second.
	 */
	AspectPropertyValue<Boolean> getSupports24p();
	/**
	 * Indicates if the device can receive and make telephone calls using available bearers without any additional software such as VoIP. Devices that support voice calls do not necessarily support phone calls.
	 */
	AspectPropertyValue<Boolean> getSupportsPhoneCalls();
	/**
	 * Indicates if the browser supports TLS or SSL, essential for secure protocols such as HTTPS.
	 */
	AspectPropertyValue<Boolean> getSupportsTlsSsl();
	/**
	 * Indicates if the browser supports WebGL technology to generate hardware-accelerated 3D graphics.
	 */
	AspectPropertyValue<Boolean> getSupportsWebGL();
	/**
	 * Indicates if the device supports Wireless Display Technology.
	 */
	AspectPropertyValue<Boolean> getSupportsWiDi();
	/**
	 * Indicates if the browser supports SVG (scalable vector graphics), useful for 2D animations and applications where all objects within the SVG can be accessed via the DOM and can have assigned event listener elements.
	 */
	AspectPropertyValue<Boolean> getSvg();
	/**
	 * Indicates the Type Allocation Code (TAC) for devices supporting GSM/3GPP networks which come from multiple sources. This property will return 'N/A' if we cannot determine the device TAC authenticy.
	 */
	AspectPropertyValue<List<String>> getTAC();
	/**
	 * Refers to the experimental Privacy Sandbox Topics API proposal from Google. Indicates if the API caller has observed one or more topics for a user and checks whether the website has not blocked the Topics API using a Permissions Policy.
	 */
	AspectPropertyValue<String> getTopicsAPIEnabled();
	/**
	 * JavaScript that overrides the property value for the TopicsAPIEnabled property.
	 */
	AspectPropertyValue<JavaScript> getTopicsAPIEnabledJavaScript();
	/**
	 * Indicates if the browser supports the method of registering and interpreting finder (or stylus) activity on touch screens or trackpads.
	 */
	AspectPropertyValue<Boolean> getTouchEvents();
	/**
	 * Indicates if the browser supports a method of tracking text being played with media, e.g. subtitles and captions.
	 */
	AspectPropertyValue<Boolean> getTrack();
	/**
	 * The matched User-Agents.
	 */
	AspectPropertyValue<List<String>> getUserAgents();
	/**
	 * Indicates if the browser supports the 'Video' element for playing videos on web pages without requiring a plug-in.
	 */
	AspectPropertyValue<Boolean> getVideo();
	/**
	 * Indicates if the browser supports Viewport, to give control over view for different screen sizes and resolutions of devices accessing a website.
	 */
	AspectPropertyValue<Boolean> getViewport();
	/**
	 * Indicates if the browser supports the WebP image format.
	 */
	AspectPropertyValue<Boolean> getWebP();
	/**
	 * Indicates if the browser supports background workers in JavaScript.
	 */
	AspectPropertyValue<Boolean> getWebWorkers();
	/**
	 * Indicates the weight of the device with battery in grams.
	 */
	AspectPropertyValue<Double> getWeightWithBattery();
	/**
	 * Indicates the weight of the device without battery in grams.
	 */
	AspectPropertyValue<Double> getWeightWithoutBattery();
	/**
	 * Indicates if the browser supports client-to-server communication with XmlHttpRequests. If the browser supports 'Xhr2' will also support 'DataForm' element. This property may need a vendor prefix, e.g. webkit, moz, etc.
	 */
	AspectPropertyValue<Boolean> getXhr2();
}
