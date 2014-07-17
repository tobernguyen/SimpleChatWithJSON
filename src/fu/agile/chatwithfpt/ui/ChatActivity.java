package fu.agile.chatwithfpt.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fu.agile.chatwithfpt.R;
import fu.agile.chatwithfpt.chatinfo.BotMessage;
import fu.agile.chatwithfpt.chatinfo.IMessage;
import fu.agile.chatwithfpt.chatinfo.UserMessage;
import fu.agile.chatwithfpt.framework.BaseActivity;
import fu.agile.chatwithfpt.services.ChatException;
import fu.agile.chatwithfpt.ui.adapter.ChatAdapter;
import fu.agile.chatwithfpt.util.App;
import fu.agile.chatwithfpt.util.Config;

public class ChatActivity extends BaseActivity implements OnClickListener {

	private final static int SPEAK_REQUEST_CODE = 12342;

	TextView mTvBotName;
	ImageView btnImgSend;
	ImageView btnImgSpeak;
	ListView mLvChat;
	EditText mEdInput;
	Context mContext;
	ProgressBar mPbBtnSend;

	ChatAdapter chatAdapter;
	List<IMessage> messageList;
	Resources resources;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		mContext = this;
		messageList = new ArrayList<IMessage>();
		resources = App.getContext().getResources();

		initViews();
	}

	private void initViews() {
		mTvBotName = (TextView) findViewById(R.id.bot_name);
		mTvBotName.setText(resources.getString(R.string.txt_chatting_with_bot)
				+ App.getChatService().getBotName());

		btnImgSend = (ImageView) findViewById(R.id.btn_send);
		btnImgSpeak = (ImageView) findViewById(R.id.btn_speak);
		btnImgSend.setOnClickListener(this);
		btnImgSpeak.setOnClickListener(this);

		mEdInput = (EditText) findViewById(R.id.txt_enter_message);
		mLvChat = (ListView) findViewById(R.id.lv_choose_bot);
		mLvChat.setClickable(false);

		mPbBtnSend = (ProgressBar) findViewById(R.id.btn_send_progess);
		// Check for speech recognition available
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			btnImgSpeak.setEnabled(false);
			Toast.makeText(
					mContext,
					resources
							.getString(R.string.speech_recognition_not_available),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View btnView) {
		switch (btnView.getId()) {
			case R.id.btn_send:
				sendMessage(mEdInput.getText().toString());
				break;
			case R.id.btn_speak:
				startVoiceRecognitionActivity();
				break;
			default:
				break;
		}
	}

	private void sendMessage(String message) {
		if (!message.equals("")) {
			new SendMessage(message).execute();
		}
	}

	public class SendMessage extends AsyncTask<Void, Void, BotMessage> {
		private ChatException exeption = null;
		private String messageToSend;

		public SendMessage(String messageToSend) {
			super();
			this.messageToSend = messageToSend;
		}

		private void enableInput(boolean enable) {
			btnImgSend.setEnabled(enable);
			btnImgSpeak.setEnabled(enable);
			if (!enable)
				mPbBtnSend.setVisibility(View.VISIBLE);
			else
				mPbBtnSend.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Disable input and button until complete sendMessage
			enableInput(false);
		}

		@Override
		protected BotMessage doInBackground(Void... params) {
			BotMessage botMessage = null;
			try {
				botMessage = App.getChatService().getBotMessage(messageToSend);
			} catch (ChatException e) {
				exeption = e;
			}

			return botMessage;
		}

		@Override
		protected void onPostExecute(BotMessage result) {
			super.onPostExecute(result);
			if (exeption != null) {
				if (Config.IS_DEBUG)
					Log.e("ChatActivity", exeption.getError().toString());
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(resources
						.getString(R.string.dialog_error_title));
				String message = "";
				switch (exeption.getError()) {
					case NetworkError:
						message = resources.getString(R.string.network_error);
						break;
					case NotFound:
						message = resources.getString(R.string.not_found_error);
						break;
					case BadRequest:
						message = resources
								.getString(R.string.bad_request_error);
						break;
					case ServerError:
						message = resources.getString(R.string.server_error);
						break;
					case Unauthorized:
						message = resources
								.getString(R.string.unauthorized_error);
						break;
					case Unknown:
						message = resources.getString(R.string.unknown_error);
						break;
					default:
						break;
				}
				builder.setMessage(message);
				builder.setPositiveButton("OK", null);
				dialog = builder.create();
				dialog.show();
			} else {
				java.util.Date date = new java.util.Date();
				java.sql.Timestamp ts = new java.sql.Timestamp(date.getTime());
				messageList.add(new UserMessage(messageToSend, ts.getTime()));

				// Fix for FPT slow in server time
				// result.setTimeStamp(ts.getTime());

				messageList.add(result);
				if (chatAdapter == null) {
					chatAdapter = new ChatAdapter(messageList, mContext);
					chatAdapter.registerDataSetObserver(new DataSetObserver() {
						@Override
						public void onChanged() {
							super.onChanged();
							mLvChat.setSelection(chatAdapter.getCount() - 1);
						}
					});
					mLvChat.setAdapter(chatAdapter);
				} else {
					chatAdapter.notifyDataSetChanged();
				}
				mEdInput.setText("");
			}
			enableInput(true);
		}

	}

	private void startVoiceRecognitionActivity() {
		String languagePref = "vi";
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
				languagePref);
		intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
				languagePref);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				resources.getString(R.string.speech_prompt));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		startActivityForResult(intent, SPEAK_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEAK_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			mEdInput.setText(matches.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
