package com.main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public class SampleClient {

	private static final String FILE_NAME = "names.txt";
	private static final String FILE_PATH = "src/main/resources/data";
	private static CacheControlDirective cache = new CacheControlDirective();
	
	/**
	 * Basic Task: Modify SampleClient so that it prints the first and last name,
	 * and birth date of each Patient to the screen
	 * Sort the output so that the results are ordered by the patient's first name
	 */
	public static void printPatientInformation() {

		Bundle response = getClient().search().forResource("Patient").where(Patient.FAMILY.matches().value("Smith"))
				.returnBundle(Bundle.class).execute();
		
		System.out.println(response);
		
		System.out.println("**************PATIENT INFORMATION*****************");
		List<BundleEntryComponent> list = response.getEntry();

		List<Patient> patients = new ArrayList<>();
		for (BundleEntryComponent entry : list) {
			if (entry != null) {
				String url = entry.getFullUrl();
				Patient patient = getClient().fetchResourceFromUrl(Patient.class, url);
				patients.add(patient);
			}
		}

		//Sorting the list of patients list by their firt name
		patients.sort((e1, e2) -> e1.getName().get(0).getGiven().get(0).toString()
				.compareTo(e2.getName().get(0).getGiven().get(0).toString()));

		for (Patient patient : patients) {
			List<HumanName> humanNames = patient.getName();
			for (HumanName name : humanNames) {

				System.out.println("FirstName: " + name.getGiven().get(0));
				System.out.println("LastName : " + name.getFamily());

				if (patient.getBirthDate() != null) {
					System.out.println("DOB      : " + new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthDate()));
				} else {
					System.out.println("DOB      : -");
				}
				System.out.println("--------------------------------------------------");
			}
		}
	}
	
	/**
	 * Method to read 20 family names from a text file and print average response time with and without cache 
	 */
	public static void averageResponseTime() {
		
		//read family names from text file
		List<String> names = readFile();
		
		//First loop of calls for all names in the text file without caching disabled
		System.out.println("*********************** LOOP 1 ***************************");
		for(String name: names) {
			getClient().search().forResource("Patient").where(Patient.FAMILY.matches().value(name)).returnBundle(Bundle.class).execute();
		}

		//Adding 2 seconds between runs
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Second loop of calls for all names in the text file without caching disabled
		System.out.println("*********************** LOOP 2 ***************************");
		for(String name: names) {
			getClient().search().forResource("Patient").where(Patient.FAMILY.matches().value(name)).returnBundle(Bundle.class).execute();
		}
		
		//Adding 2 seconds between runs
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Third loop of calls for all names in the text file with caching disabled
		System.out.println("*********************** LOOP 3 ***************************");
		
		cache.setNoCache(true);
		
		for(String name: names) {
			getClient().search().forResource("Patient").where(Patient.FAMILY.matches().value(name)).returnBundle(Bundle.class).execute();
		}
	}

	// Create a FHIR client
	public static IGenericClient getClient() {
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
		client.registerInterceptor(new LoggingInterceptor(false));
		client.registerInterceptor(new ResponseTimeInterceptor(20));
		return client;
	}
	
	/**
	 * Method to read the text file that contains 20 family names
	 * @return List of names
	 */
	public static List<String> readFile() {
		List<String> result = new ArrayList<>();
		try {
			String filePath = Paths.get(FILE_PATH, FILE_NAME).toString();
			File file = Paths.get(filePath).toFile();
			result = Files.readAllLines(Paths.get(file.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	public static void main(String[] theArgs) {

		// Basic Tasks
		// Search for Patient resources with name SMITH
		printPatientInformation();

		// Intermediate Tasks
		averageResponseTime();
	}

}
