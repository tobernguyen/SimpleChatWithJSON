package fu.agile.chatwithfpt.util;

import android.app.Application;
import android.content.Context;
import fu.agile.chatwithfpt.services.ServiceHandler;

public class App extends Application {
	static ServiceHandler serviceHandler;
	static Context currentApp;

	/** Get current application context. */
	public static final Context getContext() {
		return currentApp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (serviceHandler == null)
			serviceHandler = new ServiceHandler();
		if (currentApp == null)
			currentApp = this;
	}

	public static ServiceHandler getChatService() {
		return serviceHandler;
	}
}
