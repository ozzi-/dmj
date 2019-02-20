package model;

public class Settings {
	public static int sleepBeforeGetMails = 60;
	public static boolean debugProtocols = false;
	public static String indentMarker = "  |__ ";
	private static int commandLineArgs = 4;

	public static void checkCommandLineArgs(String[] args) {
		if (args.length < commandLineArgs) {
			System.err.println("Usage:");
			System.err.println(
					"{/path/to/config.json} {accountName} {emailSubjectContains} {/path/for/dump} {deleteAfterDump}");
			System.exit(1);
		}
	}

	public static void handleExitCode(boolean mailsFound, boolean foundAccount) {
		if (!foundAccount) {
			System.out.println("Could not find account in config json");
			System.exit(1);
		}
		if (!mailsFound) {
			System.out.println("No mails with containing keyword in subject found");
			System.exit(2);
		}
		System.exit(0);
	}
}
