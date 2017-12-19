package timeTracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import launch.Main;

public abstract class SavedData {
	public static final String DELIM = ",";

	private static File getBinFolder() {
		try {
			File root;
			String runningJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
					.replaceAll("\\\\", "/");
			int lastIndexOf = runningJarPath.lastIndexOf("/classes/");
			if (lastIndexOf < 0) {
				root = new File("");
			} else {
				root = new File(runningJarPath.substring(0, lastIndexOf) + "/bin/");
			}
			return root;
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void writeToFile(Task task, String file, Supplier<String> record) {
		writeToFile(task, file, record, true);
	}

	public static void writeToFile(Task task, String file, Supplier<String> record, boolean isAppend) {
		OpenOption options[] = new OpenOption[2];
		if (isAppend) {
			options[0] = StandardOpenOption.APPEND;
			options[1] = StandardOpenOption.CREATE;
		} else {
			options[0] = StandardOpenOption.TRUNCATE_EXISTING;
			options[1] = StandardOpenOption.CREATE;
		}
		System.out.println(System.currentTimeMillis() + DELIM + task);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(getBinFolder().getAbsolutePath() + file), options)) {
			writer.write(record.get());
			writer.newLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static List<Task> loadTasks(String file) {
		try (BufferedReader br = Files.newBufferedReader(Paths.get(getBinFolder().getAbsolutePath() + file))) {
			return br.lines().filter(line -> line.contains(DELIM)).map(line -> new Task(line.split(DELIM))).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static boolean doesFileExist(String filename) {
		return Files.exists(Paths.get(getBinFolder().getAbsolutePath() + filename));
	}
}
