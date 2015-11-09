import java.io.BufferedReader;
import java.io.IOError;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ApiClient {

	private static final int DEFAULT_RPC_PORT = 9085;
	private static final String DEFAULT_RPC_IP = "127.0.0.1";
	
	public static final String APICALLKEY = "apicallkey";


	private static List<String> allowedcalls = new CopyOnWriteArrayList<>();
	
	
	public String executeCommand(String command)
	{
		try
		{
			//SPLIT
			String[] args = command.split(" ");
			
			//GET METHOD
			String method = args[0].toUpperCase();
			
			//GET PATH
			String path = args[1];
			
			//GET CONTENT
			String content = "";
			if(method.equals("POST"))
			{
				content = command.substring((method + " " + path + " ").length());
			}
			
			//URL CANNOT CONTAIN UNICODE CHARACTERS
			String[] paths = path.split("/");
			String path2 = "";
			for (String string : paths) {
				path2 += URLEncoder.encode(string, "UTF-8") + "/";
			}
			path2 = path2.substring(0,path2.length()-1);
			
			//CREATE CONNECTION
			URL url = new URL("http://"+DEFAULT_RPC_IP+":" + DEFAULT_RPC_PORT + "/" + path2);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			//EXECUTE
			connection.setRequestMethod(method);
			
			UUID randomUUID = UUID.randomUUID();
			allowedcalls.add(randomUUID.toString());
			connection.setRequestProperty(APICALLKEY, randomUUID.toString());
			if(method.equals("POST"))
			{
				connection.setDoOutput(true);
				connection.getOutputStream().write(content.getBytes("UTF-8"));
				connection.getOutputStream().flush();
				connection.getOutputStream().close();
			}
			
			//READ RESULT
			InputStream stream;
			if(connection.getResponseCode() == 400)
			{
				stream = connection.getErrorStream();
			}
			else
			{
				stream = connection.getInputStream();
			}

			InputStreamReader isReader = new InputStreamReader(stream, "UTF-8"); 
			BufferedReader br = new BufferedReader(isReader);
			String result = br.readLine(); //TODO READ ALL OR HARDCODE HELP
			
			try
			{
				Object jsonResult = JSONValue.parse(result);
				
				if(jsonResult instanceof JSONArray)
				{
					return ((JSONArray) jsonResult).toJSONString();
					
				}
				if(jsonResult instanceof JSONObject)
				{
					return ((JSONObject) jsonResult).toJSONString();
				}
				
				return result;
			}
			catch(Exception e)
			{
				return result;
			}
			
		}
		catch(IOError ioe)
		{
			//ioe.printStackTrace();	
			return "";
		}
		catch(Exception e)
		{
			//e.printStackTrace();	
			return "";
		}
	}


	public static boolean isAllowedDebugWindowCall(String uuid) {
		return allowedcalls.contains(uuid);
	}

}
