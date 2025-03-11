package console_version;

import classes.Outputter;

public class ConsoleOutput implements Outputter {
	public void output(String s) {
		System.out.println(s);
	}
}
