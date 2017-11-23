package com.comodo.qa.browsers.tools;

import java.util.List;

import org.junit.runner.notification.Failure;

public class JunitMessageHelper {
	public static String GetPrintableMessage(List<Failure> failureList) {
		String print = "";
		for(Failure failure : failureList) {
			String message = failure.getMessage();
			print += message + ", " + System.lineSeparator();
		}
		
		if (print.endsWith(", " + System.lineSeparator())) {
			print = print.substring(0, print.length() - (2+(System.lineSeparator()).length()));
		}
		
		return print;
	}
	
}
