package fu.agile.chatwithfpt.services;

public class ChatException extends Exception {
	private static final long serialVersionUID = -5045677762312745233L;

	public static enum ERROR {
		BadRequest, Unauthorized, NotFound, ServerError, NetworkError, Unknown
	}

	private ERROR mError;

	public ChatException(ERROR errorType) {
		super();
		mError = errorType;
	}

	public ChatException(String errorCodeString) {
		this(getErrorCodeFromString(errorCodeString));
	}

	private static int getErrorCodeFromString(String errorCodeString) {
		int errorCode;
		try {
			errorCode = Integer.parseInt(errorCodeString);
		} catch (NumberFormatException e) {
			errorCode = 0;
		}
		return errorCode;
	}

	public ChatException(int errorCode) {
		switch (errorCode) {
			case 400:
				mError = ERROR.BadRequest;
				break;
			case 401:
				mError = ERROR.Unauthorized;
				break;
			case 404:
				mError = ERROR.NotFound;
				break;
			case 500:
				mError = ERROR.ServerError;
				break;
			default:
				mError = ERROR.Unknown;
				break;
		}
	}

	/**
	 * 
	 * @return enum ERROR in ChatExeption
	 */
	public ERROR getError() {
		return mError;
	}
}
