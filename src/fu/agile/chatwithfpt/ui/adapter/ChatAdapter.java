package fu.agile.chatwithfpt.ui.adapter;

import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import fu.agile.chatwithfpt.R;
import fu.agile.chatwithfpt.chatinfo.IMessage;
import fu.agile.chatwithfpt.chatinfo.UserMessage;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ChatAdapter extends BaseAdapter {

	List<IMessage> messageList;
	Context mContext;
	private static final int TYPE_MAX_COUNT = 2;
	private static final int TYPE_USER_MSG = 0;
	private static final int TYPE_BOT_MSG = 1;
	
	public ChatAdapter(List<IMessage> messageList, Context mContext) {
		super();
		this.messageList = messageList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return messageList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return messageList.get(position) instanceof UserMessage?TYPE_USER_MSG:TYPE_BOT_MSG;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		IMessage message = messageList.get(position);
		ViewHolder holder;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
				case TYPE_USER_MSG:
					convertView = View.inflate(mContext, R.layout.user_chat_bubble,
							null);
					break;
				case TYPE_BOT_MSG:
					convertView = View.inflate(mContext, R.layout.bot_chat_bubble,
							null);
					break;
			}
			holder = new ViewHolder();
			holder.mTvMessage = (TextView) convertView
					.findViewById(R.id.txt_enter_message);
			holder.mTvTime = (TextView) convertView.findViewById(R.id.bot_language);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mTvMessage.setText(message.getMessage());
		holder.mTvTime.setText(convertTime(message.getTimeStamp()));
		return convertView;
	}

	private String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("dd-MMM-yyyy K:mm a");
	    return format.format(date);
	}
	
	public class ViewHolder {
		TextView mTvMessage;
		TextView mTvTime;
	}

}
