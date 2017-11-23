package com.comodo.qa.browsers.tools;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ProcessHelper
{
    public static void runApplication(String applicationFilePath) throws IOException, InterruptedException
    {
        File application = new File(applicationFilePath);
        String applicationName = application.getName();

        if (!isProcessRunning(applicationName))
        {
            Desktop.getDesktop().open(application);
        }
    }

    // http://stackoverflow.com/a/19005828/3764804
    public static boolean isProcessRunning(String processName) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe");
        Process process = processBuilder.start();
        String tasksList = toString(process.getInputStream());

        return tasksList.contains(processName.substring(0, Math.min(processName.length(), 25)));
    }

    // http://stackoverflow.com/a/5445161/3764804
    public static String toString(InputStream inputStream)
    {
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        String string = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        return string;
    }
}