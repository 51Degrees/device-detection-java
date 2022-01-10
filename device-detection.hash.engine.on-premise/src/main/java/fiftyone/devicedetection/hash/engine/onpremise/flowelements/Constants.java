package fiftyone.devicedetection.hash.engine.onpremise.flowelements;

public class Constants {
	public static class MatchMetrics {
		/* Category */
		public static final String CATEGORY = "Device Metrics";
		
		/* Names */
		public static final String MATCHED_NODES = "MatchedNodes";
		public static final String DIFFERENCE = "Difference";
		public static final String DRIFT = "Drift";
		public static final String DEVICE_ID = "DeviceId";
		public static final String USER_AGENTS = "UserAgents";
		public static final String METHOD = "Method";
		public static final String ITERATIONS = "Iterations";
		
		/* Match metric properties description */
		public static final String MATCHED_NODES_DESCRIPTION = "Indicates the number of " +
			"hash nodes matched within the evidence.";
		public static final String DIFFERENCE_DESCRIPTION = "Used when detection method " +
			"is not Exact or None. This is an integer value and the larger the " +
			"value the less confident the detector is in this result.";
		public static final String DRIFT_DESCRIPTION = "Total difference in character " +
			"positions where the substrings hashes were found away from where " +
			"they were expected.";
		public static final String DEVICE_ID_DESCRIPTION = "Consists of four components " +
			"separated by a hyphen symbol: Hardware-Platform-Browser-IsCrawler " +
			"where each Component represents an ID of the corresponding Profile.";
		public static final String USER_AGENTS_DESCRIPTION = "The matched User-Agents.";
		public static final String METHOD_DESCRIPTION = "Provides information about " +
			"the algorithm that was used to perform detection for a particular " +
			"User-Agent.";
		public static final String ITERATIONS_DESCRIPTION = "The number of iterations " +
			"carried out in order to find a match. This is the number of nodes " +
			"in the graph which have been visited.";
	}
}
