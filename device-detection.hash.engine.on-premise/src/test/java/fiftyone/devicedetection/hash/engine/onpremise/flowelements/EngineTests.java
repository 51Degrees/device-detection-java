/* *********************************************************************
 * This Original Work is copyright of 51 Degrees Mobile Experts Limited.
 * Copyright 2026 51 Degrees Mobile Experts Limited, Davidson House,
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

package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import fiftyone.devicedetection.shared.testhelpers.FileUtils;
import fiftyone.pipeline.engines.Constants;
import fiftyone.pipeline.engines.services.DataUpdateService;
import fiftyone.pipeline.engines.services.DataUpdateServiceDefault;

import static fiftyone.devicedetection.shared.testhelpers.FileUtils.LITE_HASH_DATA_FILE_NAME;
import static fiftyone.devicedetection.shared.testhelpers.FileUtils.TAC_HASH_DATA_FILE_NAME;
import static fiftyone.pipeline.util.FileFinder.getFilePath;

public class EngineTests {
	protected static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

	private DeviceDetectionHashEngine createEngine(File dataFile, DataUpdateService dataUpdateService)
			throws Exception {
		return new DeviceDetectionHashEngineBuilder(loggerFactory, dataUpdateService)
				.setPerformanceProfile(Constants.PerformanceProfiles.HighPerformance)
				.setUpdateMatchedUserAgent(true)
				.setAutoUpdate(false)
				.build(dataFile.toString(), false);
	}

	/**
	 * Shared method for dataDataSourceTier tests.
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	private void testDataSourceTier(String fileName) throws Exception {
		File dataFile = null;
		try {
			dataFile = getFilePath(fileName);
		} catch (IllegalArgumentException e) {
			assumeTrue(e.getMessage(), false);
		}

		DataUpdateService dataUpdateService = new DataUpdateServiceDefault();
		try {
			DeviceDetectionHashEngine engine = createEngine(dataFile, dataUpdateService);
			String tier = engine.getDataSourceTier();
			engine.close();

			if (fileName.equals(LITE_HASH_DATA_FILE_NAME)) {
				assertEquals("Lite", tier);
			} else {
				assertTrue(tier.equalsIgnoreCase("Enterprise") ||
						tier.equalsIgnoreCase("TAC"));
			}
		} finally {
			dataUpdateService.close();
		}
	}

	/**
	 * Check that getDataSourceTier returns correct product for Lite data file
	 * 
	 * @throws Exception
	 */
	@Test
	public void Engine_Hash_GetDataSourceTier_Lite() throws Exception {
		testDataSourceTier(LITE_HASH_DATA_FILE_NAME);
	}

	/**
	 * Check that getDataSourceTier returns correct product for Enterprise data
	 * file
	 * 
	 * @throws Exception
	 */
	@Test
	public void Engine_Hash_GetDataSourceTier_Enterprise() throws Exception {
		testDataSourceTier(TAC_HASH_DATA_FILE_NAME);
	}

}
