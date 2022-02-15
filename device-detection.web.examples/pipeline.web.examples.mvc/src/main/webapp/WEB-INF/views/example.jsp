<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Example page</title>
<script src="51Degrees.core.js"></script>
</head>
<body>

<h2>Example</h2>

<div id="content">
    <p>
        Hardware Vendor: ${hardwareVendor.hasValue() ? hardwareVendor.getValue() : "Unknown: ".concat(hardwareVendor.getNoValueMessage())}<br />
        Hardware Name: ${hardwareName.hasValue() ? String.join(", ", hardwareName.getValue()) : "Unknown: ".concat(hardwareName.getNoValueMessage())}<br />
        Device Type: ${deviceType.hasValue() ? deviceType.getValue() : "Unknown: ".concat(deviceType.getNoValueMessage())}<br />
        Platform Vendor: ${platformVendor.hasValue() ? platformVendor.getValue() : "Unknown: ".concat(platformVendor.getNoValueMessage())}<br />
        Platform Name: ${platformName.hasValue() ? platformName.getValue() : "Unknown: ".concat(platformName.getNoValueMessage())}<br />
        Platform Version: ${platformVersion.hasValue() ? platformVersion.getValue() : "Unknown: ".concat(platformVersion.getNoValueMessage())}<br />
        Browser Vendor: ${browserVendor.hasValue() ? browserVendor.getValue() : "Unknown: ".concat(browserVendor.getNoValueMessage())}<br />
        Browser Name: ${browserName.hasValue() ? browserName.getValue() : "Unknown: ".concat(browserName.getNoValueMessage())}<br />
        Browser Version: ${browserVersion.hasValue() ? browserVersion.getValue() : "Unknown: ".concat(browserVersion.getNoValueMessage())}<br />
        Device JavaScript : ${javascripthardwareprofile.hasValue() ? javascripthardwareprofile.getValue() : "Unknown: ".concat(javascripthardwareprofile.getNoValueMessage())}
    </p>
</div>

<script>
    // This function will fire when the JSON data object is updated
    // with information from the server.
    // The sequence is:
    // 1. Response contains JavaScript property 'getLatitude' that gets executed on the client
    // 2. This triggers another call to the webserver that passes the location as evidence
    // 3. The web server responds with new JSON data that contains the hemisphere based on the location.
    // 4. The JavaScript integrates the new JSON data and fires the onChange callback below.
    window.onload = function () {
        fod.complete(function (data) {
            var para = document.createElement("p");
            var br = document.createElement("br");
            var text = document.createTextNode("Updated information from client-side evidence:");
            para.appendChild(text);
            para.appendChild(br);
            text = document.createTextNode("Hardware Name: " + data.device.hardwarename.join(","));
            br = document.createElement("br");
            para.appendChild(text);
            para.appendChild(br);
            text = document.createTextNode("Screen width (pixels): " + data.device.screenpixelswidth);
            br = document.createElement("br");
            para.appendChild(text);
            para.appendChild(br);
            text = document.createTextNode("Screen height (pixels): " + data.device.screenpixelsheight);
            br = document.createElement("br");
            para.appendChild(text);
            para.appendChild(br);

            var element = document.getElementById("content");
            element.appendChild(para);
        });
    }
</script></body>
</html>