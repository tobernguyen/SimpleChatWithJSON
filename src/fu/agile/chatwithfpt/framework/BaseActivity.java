package fu.agile.chatwithfpt.framework;

import android.app.AlertDialog;

import com.fpt.robot.app.RobotActivity;

public class BaseActivity extends RobotActivity {
	protected AlertDialog dialog;

	@Override
	public void onDestroy() {
		try{
			super.onDestroy();
		}catch (Exception e){
			
		}
		
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
