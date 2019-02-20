package ua.net.maxx.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import ua.net.maxx.Application;

public class AppRestarter {

	public static void restartApplication() throws IOException, URISyntaxException {

		String javaBin = System.getProperty("JAVA_HOME") + File.separator + "bin" + File.separator + "java";
		javaBin = "java";
		
		final File currentJar = new File(
				Application.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		/* is it a jar file? */
		if (!currentJar.getName().endsWith(".jar"))
			return;

		/* Build command: java -jar application.jar */
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.start();
		System.exit(0);
	}

}
