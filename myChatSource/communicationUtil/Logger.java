package communicationUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

	public enum Level {
		INFO('I'), WARNING('W'), DEBUG('D');

		private char letter;

		Level(char letter) {
			this.letter = letter;
		}

		public char getLetter() {
			return letter;
		}
	}

	private static Level level = Level.INFO; // Default value

	private static Logger instance;

	private static FileWriter writer;

	private Logger() {
		try {
			File file = new File("log.txt");
			if (file.createNewFile()) {
				System.out.println("Logger: File created: " + file.getName());
			} else {
				System.out.println("Logger: File already exists.");
				file.delete();
				System.out.println("Logger: File has been deleted.");
				file = new File("log.txt");
				System.out.println("Logger: File created: " + file.getName());
			}

			writer = new FileWriter("log.txt", false);
			writer.write("######################## NEW RUN ############################\n");

		} catch (IOException e) {
			System.out.println("Logger: An error occurred.");
		}
	}

	public static void stop() {
		if (writer != null)
			try {
				writer.close();
				System.out.println("Logger: file writer is closed");
			} catch (IOException e) {
				System.out.println("Logger: failed to close file writer");
			}
	}

	public static Logger init() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	public static void setLevel(Level l) {
		System.out.println("Logger: level set is: " + l);
		level = l;
	}

	private static void outputLog(String msg, char letter) {

		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		StringBuilder str = new StringBuilder(date.format(dateFormat) + " " + letter + " " + msg);

		if (msg.charAt(msg.length() - 1) != '\n') {
			str.append('\n');
		}

		try {
			System.out.println("Logger: writing to file: " + str.toString());
			writer.write(str.toString());
			writer.flush();
		} catch (IOException e) {
			System.out.println("Logger: failed to write: " + msg);
		}
	}

	public static void log(Level l, String msg) {

		if (level == Level.DEBUG)
			outputLog(msg, l.getLetter());
		else if (level == Level.INFO && (l == Level.INFO || l == Level.WARNING))
			outputLog(msg, l.getLetter());
		else if (level == Level.WARNING && l == Level.WARNING)
			outputLog(msg, l.getLetter());
	}
}
