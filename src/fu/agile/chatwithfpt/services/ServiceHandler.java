package fu.agile.chatwithfpt.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fu.agile.chatwithfpt.chatinfo.BotInfo;
import fu.agile.chatwithfpt.chatinfo.BotMessage;
import fu.agile.chatwithfpt.services.ChatException.ERROR;
import fu.agile.chatwithfpt.util.Config;

public class ServiceHandler {

	public static final String TOKEN = "d57c03f8-e174-4b20-b113-685f202e15b1";
	public static final String REQUEST_URL = "http://tech.fpt.com.vn/AIML/api/bots";
	public static final String PARAM_TOKEN = "token";
	public static final String PARAM_BOT_ID = "botID";
	public static final String PARAM_MESSAGE_DATA = "request";
	public static final String TAG_STATUS_ON_QUERY_BOT = "result";
	public static final String TAG_STATUS_ON_QUERY_MESSAGE = "status";
	public static final String TAG_BOTS = "bots";
	public static final String TAG_BOT_ID = "id";
	public static final String TAG_BOT_NAME = "name";
	public static final String TAG_BOT_LANGUAGE = "language";

	public static final String TAG_RESPONSE = "response";
	public static final String TAG_TIME_STAMP = "time_stamp";

	private String botId;
	private String botName;

	public ServiceHandler() {
		botId = "";
		botName = "";
	}

	private String makeServiceCall(String url, List<NameValuePair> params)
			throws ChatException {
		String response = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			if (params != null) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
			}

			if (Config.IS_DEBUG)
				Log.e("ServiceHandler", "Request Url: " + url);

			HttpGet httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
		} catch (IOException e) {
			throw new ChatException(ChatException.ERROR.NetworkError);
		}

		return response;
	}

	/**
	 * getBotList
	 * 
	 * @return the List of all Bot Availabe
	 * @throws ChatException
	 **/
	public List<BotInfo> getBotList() throws ChatException {
		List<BotInfo> botList = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_TOKEN, TOKEN));
		String jsonStr = makeServiceCall(REQUEST_URL, params);

		if (jsonStr != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);

				if (jsonObject.getString(TAG_STATUS_ON_QUERY_BOT).equals(
						"success")) {
					botList = new ArrayList<BotInfo>();
					// Getting JSON Array
					JSONArray bots = jsonObject.getJSONArray(TAG_BOTS);
					for (int i = 0; i < bots.length(); i++) {
						JSONObject bot = bots.getJSONObject(i);
						String id = bot.getString(TAG_BOT_ID);
						String name = bot.getString(TAG_BOT_NAME);
						String language = bot.getString(TAG_BOT_LANGUAGE);
						botList.add(new BotInfo(id, name, language));
					}
				} else {
					JSONObject error = jsonObject.getJSONObject("data");
					String error_code = error.getString("error_code");
					throw new ChatException(error_code);
				}

			} catch (JSONException e) {
				throw new ChatException(ERROR.Unknown);
			}
		}
		return botList;
	}

	/**
	 * getAnswer
	 * 
	 * @return BotMessage which response from server
	 * @throws ChatException
	 **/
	public BotMessage getBotMessage(String sendMsg) throws ChatException {
		BotMessage botMessage = null;
		String newRequestUrl = REQUEST_URL + "/" + botId + "/chat";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_MESSAGE_DATA, sendMsg));
		params.add(new BasicNameValuePair(PARAM_TOKEN, TOKEN));

		String jsonStr = makeServiceCall(newRequestUrl, params);

		if (jsonStr != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);

				if (jsonObject.getString(TAG_STATUS_ON_QUERY_MESSAGE).equals(
						"success")) {
					String response = jsonObject.getString(TAG_RESPONSE);
					String timeStampString = jsonObject
							.getString(TAG_TIME_STAMP);
					long timeStamp = Long.parseLong(timeStampString);
					botMessage = new BotMessage(response, timeStamp);
				} else {
					JSONObject error = jsonObject.getJSONObject("data");
					String error_code = error.getString("error_code");
					throw new ChatException(error_code);
				}

			} catch (JSONException e) {
				if (Config.IS_DEBUG) {
					Log.e("ServiceHandler", e.getMessage());
				}
				throw new ChatException(ERROR.Unknown);
			}
		}
		return botMessage;
	}

	/**
	 * 
	 * @param botId
	 *            : set bot id to use in the whole application
	 */
	public void setBotId(String botId) {
		this.botId = botId;
	}

	/**
	 * 
	 * @return botId
	 */
	public String getBotId() {
		return botId;
	}

	/**
	 * @return the botName
	 */
	public String getBotName() {
		return botName;
	}

	/**
	 * @param botName
	 *            the botName to set
	 */
	public void setBotName(String botName) {
		this.botName = botName;
	}

}
