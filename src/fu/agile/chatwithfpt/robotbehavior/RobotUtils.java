package fu.agile.chatwithfpt.robotbehavior;

import com.fpt.robot.Robot;
import com.fpt.robot.RobotException;
import com.fpt.robot.app.RobotActivity;
import com.fpt.robot.tts.RobotTextToSpeech;

public class RobotUtils {
	private RobotActivity mRobotAc;
	private Robot mRobot;

	public RobotUtils(RobotActivity mRobotAc) {
		super();
		this.mRobotAc = mRobotAc;
	}

	public void initRobot() {
		if (mRobot == null)
			mRobotAc.scan();
		mRobot = mRobotAc.getRobot();
	}

	public synchronized void speakText(final String statement) {
		if (mRobot != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						// say text with VietNamese language
						RobotTextToSpeech.say(mRobot, statement,
								RobotTextToSpeech.ROBOT_TTS_LANG_VI);
					} catch (RobotException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}else initRobot();

	}

}
