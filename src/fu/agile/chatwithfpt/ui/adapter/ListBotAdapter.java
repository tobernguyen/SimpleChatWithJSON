package fu.agile.chatwithfpt.ui.adapter;

import java.util.List;

import fu.agile.chatwithfpt.R;
import fu.agile.chatwithfpt.chatinfo.BotInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListBotAdapter extends BaseAdapter {
	List<BotInfo> botList;
	Context mContext;

	public ListBotAdapter(List<BotInfo> botList, Context mContext) {
		this.botList = botList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return botList.size();
	}

	@Override
	public BotInfo getItem(int arg0) {
		return botList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.bot_list_item, null);
			holder = new ViewHolder();
			holder.txtBotName = (TextView) convertView
					.findViewById(R.id.bot_name);
			holder.txtBotLanguage = (TextView) convertView
					.findViewById(R.id.bot_language);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BotInfo item = getItem(position);
		holder.txtBotName.setText(item.getBotName());
		holder.txtBotLanguage.setText(item.getBotLanguage());
		return convertView;
	}

	public class ViewHolder {
		public TextView txtBotName;
		public TextView txtBotLanguage;
	}

}
