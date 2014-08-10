package fu.agile.chatwithfpt.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fu.agile.chatwithfpt.R;
import fu.agile.chatwithfpt.chatinfo.BotInfo;
import fu.agile.chatwithfpt.services.ChatException;
import fu.agile.chatwithfpt.services.ServiceHandler;
import fu.agile.chatwithfpt.ui.adapter.ListBotAdapter;
import fu.agile.chatwithfpt.util.App;

public class MainActivity extends Activity {

	ListView mLvChooseBot;
	List<BotInfo> botList;
	Context mContext;
	ServiceHandler mServiceHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mServiceHandler = App.getChatService();
		initViews();
		new GetBotList().execute();
	}

	private void initViews() {
		mContext = this;
		mLvChooseBot = (ListView) findViewById(R.id.lv_choose_bot);
		mLvChooseBot.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg) {
				BotInfo item = (BotInfo) mLvChooseBot
						.getItemAtPosition(position);
				mServiceHandler.setBotId(item.getBotId());
				mServiceHandler.setBotName(item.getBotName());
				Intent goToChatActivity = new Intent(MainActivity.this,
						ChatActivity.class);
				startActivity(goToChatActivity);
			}
		});
	}

	private class GetBotList extends AsyncTask<Void, Void, Void> {
		private ChatException exeption = null;
		private Resources resource;
		private AlertDialog dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			resource = mContext.getResources();
			// Showing progress dialog
			dialog = new ProgressDialog(mContext);
			dialog.setMessage(resource.getString(R.string.please_wait));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				botList = mServiceHandler.getBotList();
			} catch (ChatException e) {
				exeption = e;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dialog.isShowing())
				dialog.dismiss();
			if (exeption != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(resource
						.getString(R.string.dialog_error_title));
				String message = "";
				switch (exeption.getError()) {
					case NetworkError:
						message = resource.getString(R.string.network_error);
						break;
					case NotFound:
						message = resource.getString(R.string.not_found_error);
						break;
					case BadRequest:
						message = resource
								.getString(R.string.bad_request_error);
						break;
					case ServerError:
						message = resource.getString(R.string.server_error);
						break;
					case Unauthorized:
						message = resource
								.getString(R.string.unauthorized_error);
						break;
					case Unknown:
						message = resource.getString(R.string.unknown_error);
						break;
					default:
						break;
				}
				builder.setMessage(message);
				builder.setPositiveButton("OK", null);
				dialog = builder.create();
				dialog.show();
			} else {
				ListBotAdapter adapter = new ListBotAdapter(botList, mContext);
				mLvChooseBot.setAdapter(adapter);
			}
		}

	}

}
