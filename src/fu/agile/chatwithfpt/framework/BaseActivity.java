package fu.agile.chatwithfpt.framework;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	protected AlertDialog dialog;

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
