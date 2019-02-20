package mail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import model.MailAccount;
import model.Settings;

public class JSON {

	public static ArrayList<MailAccount> loadConfig(String path) {
		String json = "";
		try {
			json = Helper.readFile(path);
		} catch (IOException e) {
			System.err.println("Cannot load config json file!");
			System.err.println(e.toString());
			System.exit(1);
		}
		ArrayList<MailAccount> mal = new ArrayList<MailAccount>();
		final JSONObject obj;
		String curItem = "null";
		System.out.println("Loading Mail Accounts");
		try {
			obj = new JSONObject(json);
			String[] items = JSONObject.getNames(obj);
			for (String item : items) {
				curItem = item;
				if(item.equals("settings")) {
					parseSettings(obj);
				}else {
					JSONObject mailAccountJSON = (JSONObject) obj.get(item);
					String address = mailAccountJSON.getString("address");
					String login = mailAccountJSON.getString("login");
					String pw = mailAccountJSON.getString("pw");
					String inboxFolderName = mailAccountJSON.getString("inbox_folder_name");
					String hostSmtp;
					String hostImap;
					if(exists(mailAccountJSON, "host")) {
						String host = mailAccountJSON.getString("host");
						hostSmtp = host;
						hostImap = host;
					}else {
						hostSmtp = mailAccountJSON.getString("host_smtp");
						hostImap = mailAccountJSON.getString("host_imap");
					}
					int portSmtp = mailAccountJSON.getInt("port_smtp");
					int portImap = mailAccountJSON.getInt("port_imap");
					boolean secureSmtp;
					boolean secureImap;
					if(exists(mailAccountJSON, "secure")) {
						boolean secure = mailAccountJSON.getBoolean("secure");
						secureSmtp = secure;
						secureImap = secure;
					}else {
						secureSmtp = mailAccountJSON.getBoolean("secure_smtp");
						secureImap = mailAccountJSON.getBoolean("secure_imap");						
					}

					boolean trustAllCerts = exists(mailAccountJSON, "trust_all_certs")?mailAccountJSON.getBoolean("trust_all_certs"):false;
					mal.add(new MailAccount(item, address, login, pw, inboxFolderName, hostSmtp, portSmtp, secureSmtp, hostImap, portImap, secureImap, trustAllCerts));
					System.out.println(Settings.indentMarker+item+" ("+address+")");
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing config json file at object \""+curItem+"\"");
			System.err.println(e.toString());
			System.exit(2);
		}
	    return mal;
	}
	
	private static boolean exists(JSONObject obj, String key) {
		String[] items = JSONObject.getNames(obj);
		for (String item : items) {
			if(item.equals(key)) {
				return true;
			}
		}
		return false;
	}

	private static void parseSettings(final JSONObject obj) {
		JSONObject settings = (JSONObject) obj.get("settings");
		if(exists(settings, "sleep_before_get_mails")) {
			Settings.sleepBeforeGetMails = settings.getInt("sleep_before_get_mails");			
		}
		if(exists(settings, "debug_protocols")) {
			Settings.debugProtocols = settings.getBoolean("debug_protocols");
		}
	}
	
	static void parseConfig(Map<String, String> config, final JSONObject obj, String name) {
		try {
			String value = String.valueOf(obj.get(name));
			config.put(name,  value);			
		}catch(Exception e) {
			System.err.println("Cannot parse "+name+" from config json file.   ("+e.getMessage()+")");
			System.exit(1);			
		}
	}
	
	public static class Helper {
		static String readFile(String path) throws IOException {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, "UTF-8");
		}
	}
}
