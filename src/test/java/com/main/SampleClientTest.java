package com.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ca.uhn.fhir.rest.client.api.IGenericClient;

@RunWith(JUnit4.class)
public class SampleClientTest {
	
	@Test
	public void testReadFile() {
		List<String> actual = SampleClient.readFile();
		List<String> expected = Arrays.asList("SMITH", "JOHN", "PAM", "MARIA", "SAM", "ANDY", "JOHNSON", "MILLER",
				"WILLIAMS", "JAMES", "BROWN", "LOPEZ", "GARCIA", "DORA", "TRUMP", "BIDEN", "HARRIS", "PENCE", "CARTER",
				"WARREN");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetClient() {
		SampleClient.getClient();
		assertTrue(SampleClient.getClient() instanceof IGenericClient);
	}
	
	@Test
	public void testAverageResponseTime() {
		SampleClient.averageResponseTime();
	}
}
