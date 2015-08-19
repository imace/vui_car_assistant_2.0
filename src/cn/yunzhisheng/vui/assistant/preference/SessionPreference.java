package cn.yunzhisheng.vui.assistant.preference;

public class SessionPreference {
	/********************** session key *********************/
	// common
	public static final String KEY_DOMAIN = "domain";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ORIGIN_TYPE = "origin_type";
	public static final String KEY_ORIGIN_CODE = "origin_code";
	public static final String KEY_DATA = "data";
	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";
	public static final String KEY_TTS_ANSWER = "tts";
	public static final String KEY_RESULT = "result";
	public static final String KEY_ON_CANCEL = "onCancel";
	public static final String KEY_ON_OK = "onOK";
	public static final String KEY_URL = "url";
	public static final String KEY_KEYWORD = "keyword";
	public static final String KEY_CLASS_NAME = "class_name";
	public static final String KEY_PACKAGE_NAME = "package_name";

	public static final String KEY_CATEGORY = "category";
	public static final String KEY_NAME = "name";
	// tv
	public static final String KEY_CHANNEL = "channel";
	public static final String KEY_BYDATE = "byDate";
	public static final String KEY_DATE = "date";
	public static final String KEY_PROGRAMS = "programs";
	public static final String KEY_PID = "pid";
	public static final String KEY_TIME = "time";
	public static final String KEY_TITLE = "title";
	public static final String KEY_PERIOD = "period";
	public static final String KEY_BROADCASTS = "broadcasts";
	public static final String KEY_SCORE = "score";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_VENDOR = "vendor";
	public static final String KEY_PIC = "pic";

	public static final String KEY_PROTOCAL = "protocal";
	public static final String KEY_STATION = "station";
	public static final String KEY_MSG = "msg";
	public static final String KEY_CODE = "code";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_CHANNEL_LIST = "channelList";

	public static final String KEY_FREQUENCY_LIST = "frequencyList";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_UNIT = "unit";
	public static final String KEY_UNSUPPROT_TEXT = "unsupportText";
	public static final String KEY_ERROR_CODE = "errorcode";

	/********************** session value *********************/
	public static final String VALUE_SESSION_BENGIN = "SESSION_BEGIN";
	public static final String VALUE_SESSION_SHOW = "SESSION_SHOW";
	public static final String VALUE_SESSION_END = "SESSION_END";

	public static final String VALUE_TYPE_MUTIPLE_UNSUPPORT = "MUTIPLE_UNSUPPORT";
	public static final String VALUE_TYPE_CONFIRM_UNSUPPORT = "CONFIRM_UNSUPPORT";
	public static String VALUE_TYPE_UNSUPPORT_BEGIN_SHOW = "UNSUPPORT_BEGIN_SHOW";
	public static String VALUE_TYPE_UNSUPPORT_END_SHOW = "UNSUPPORT_END_SHOW";

	public static final String VALUE_TYPE_WAITING = "WAITING";
	public static final String VALUE_TYPE_WEATHER_SHOW = "WEATHER_SHOW";
	public static final String VALUE_TYPE_WEB_SHOW = "WEB_SHOW";
	public static final String VALUE_TYPE_TRANSLATION_SHOW = "TRANSLATION_SHOW";
	public static final String VALUE_TYPE_STOCK_SHOW = "STOCK_SHOW";
	public static final String VALUE_TYPE_MUSIC_SHOW = "MUSIC_SHOW";
	public static final String VALUE_TYPE_PROG_RECOMMEND = "PROG_RECOMMEND";
	public static final String VALUE_TYPE_PROG_SEARCH_RESULT = "PROG_SEARCH_RESULT";
	public static final String VALUE_TYPE_CHANNEL_PROG_LIST = "CHANNEL_PROG_LIST";
	public static final String VALUE_TYPE_ROUTE_SHOW = "ROUTE_SHOW";
	public static final String VALUE_TYPE_POSITION_SHOW = "POSITION_SHOW";
	public static final String VALUE_TYPE_TALK_SHOW = "TALK_SHOW";
	public static final String VALUE_TYPE_ERROR_SHOW = "ERROR_SHOW";
	public static final String VALUE_TYPE_APP_LAUNCH = "APP_LAUNCH";
	public static final String VALUE_TYPE_APP_UNINSTALL = "APP_UNINSTALL";
	public static final String VALUE_TYPE_APP_EXIT = "APP_EXIT";
	public static final String VALUE_TYPE_SETTING = "SETTING_SHOW";
	public static final String VALUE_TYPE_REMINDER_CONFIRM = "REMINDER_SHOW";
	public static final String VALUE_TYPE_REMINDER_OK = "REMINDER_OK";
	public static final String VALUE_TYPE_POI_SHOW = "POI_SHOW";
	public static final String VALUE_TYPE_MULTIPLE_SHOW = "MULTIPLE_SHOW";
	public static final String VALUE_TYPE_UI_HANDLE_SHOW = "UI_HANDLE_SHOW";
	// note
	public static final String VALUE_TYPE_NOTE_SHOW = "NOTE_SHOW";
	// alarm
	public static final String VALUE_TYPE_ALARM_SHOW = "ALARM_SHOW";
	// multiple app
	public static final String VALUE_TYPE_APP_MUTIPLEP_SHOW = "MUTIPLE_APP";
	// sms
	public static final String VALUE_TYPE_SMS_CANCEL = "SMS_CANCEL";
	public static final String VALUE_TYPE_SMS_OK = "SMS_OK";
	public static final String VALUE_TYPE_SMS_READ = "SMS_READ";

	// contact
	public static final String VALUE_TYPE_CONTACT_SHOW = "CONTACT_SHOW";
	public static final String VALUE_TYPE_CONTACT_ADD = "CONTACT_ADD";

	// code
	public static final String VALUE_CODE_ANSWER = "ANSWER";
	public static final String VALUE_TYPE_CHANNEL_SWITCH_SHOW = "CHANNEL_SWITCH_SHOW";
	public static final String VALUE_TYPE_SHOP_SHOW = "SHOP_SHOW";
	public static final String VALUE_TYPE_BROADCAST_SHOW = "BROADCAST_SHOW";

	// route
	public static final String VALUE_ROUTE_METHOD_UNKNOWN = "ROUTE_METHOD_UNKNOWN";
	public static final String VALUE_ROUTE_METHOD_BUS = "BUS";
	public static final String VALUE_ROUTE_METHOD_WALK = "WALK";
	public static final String VALUE_ROUTE_METHOD_CAR = "CAR";

	public static final String VALUE_MUTIPLE_LOCATION = "MUTIPLE_LOCATION";

	// setting
	public static final String VALUE_SETTING_ACT_OPEN = "ACT_OPEN";
	public static final String VALUE_SETTING_ACT_CLOSE = "ACT_CLOSE";
	public static final String VALUE_SETTING_ACT_INCREASE = "ACT_INCREASE";
	public static final String VALUE_SETTING_ACT_DECREASE = "ACT_DECREASE";
	public static final String VALUE_SETTING_ACT_MAX = "ACT_MAX";
	public static final String VALUE_SETTING_ACT_MIN = "ACT_MIN";
	public static final String VALUE_SETTING_ACT_CLEAR = "ACT_CLEAR";
	public static final String VALUE_SETTING_ACT_SET = "ACT_SET";
	public static final String VALUE_SETTING_ACT_LOOKUP = "ACT_LOOKUP";
	public static final String VALUE_SETTING_ACT_OPEN_CHANNEL = "ACT_OPEN_CHANNEL";
	public static final String VALUE_SETTING_ACT_RECEIVED = "ACT_RECEIVED";
	public static final String VALUE_SETTING_ACT_MISSED = "ACT_MISSED";
	public static final String VALUE_SETTING_ACT_REDIAL = "ACT_REDIAL";
	public static final String VALUE_SETTING_ACT_DIALED = "ACT_DIALED";
	public static final String VALUE_SETTING_ACT_ANSWER = "ACT_ANSWER";
	public static final String VALUE_SETTING_ACT_HANG_UP = "ACT_HANG_UP";
	public static final String VALUE_SETTING_ACT_MATCH = "ACT_MATCH";
	public static final String VALUE_SETTING_ACT_DISCONNECTED = "ACT_DISCONNECTED";
	public static final String VALUE_SETTING_ACT_CANCEL = "ACT_CANCEL";
	public static final String VALUE_SETTING_ACT_PLAY = "ACT_PLAY";
	public static final String VALUE_SETTING_ACT_PAUSE = "ACT_PAUSE";
	public static final String VALUE_SETTING_ACT_STOP = "ACT_STOP";
	public static final String VALUE_SETTING_ACT_RESUME = "ACT_RESUME";
	public static final String VALUE_SETTING_ACT_PREV = "ACT_PREV";
	public static final String VALUE_SETTING_ACT_NEXT = "ACT_NEXT";
	public static final String VALUE_SETTING_ACT_SWITCH = "ACT_SWITCH";
	public static final String VALUE_SETTING_ACT_SET_DESTINATION = "ACT_SET_DESTINATION";
	public static final String VALUE_SETTING_ACT_GO_BACK_HOME = "ACT_GO_BACK_HOME";
	public static final String VALUE_SETTING_ACT_GO_BACK_COMPANY = "ACT_GO_BACK_COMPANY";
	public static final String VALUE_SETTING_ACT_TURN_ON = "ACT_TURN_ON";
	public static final String VALUE_SETTING_ACT_TURN_OFF = "ACT_TURN_OFF";
	public static final String VALUE_SETTING_ACT_REDUCE_SPEED = "ACT_REDUCE_SPEED";
	public static final String VALUE_SETTING_ACT_DEFROST = "ACT_DEFROST";
	public static final String VALUE_SETTING_ACT_ACT_DEMIST = "ACT_DEMIST";
	public static final String VALUE_SETTING_ACT_BLOW_WINDOW = "ACT_BLOW_WINDOW";
	public static final String VALUE_SETTING_ACT_BLOW_FACE = "ACT_BLOW_FACE";
	public static final String VALUE_SETTING_ACT_READ = "ACT_READ";
	public static final String VALUE_SETTING_ACT_UPLOAD = "ACT_UPLOAD";
	public static final String VALUE_SETTING_ACT_PLAY_MESSAGE = "ACT_PLAY_MESSAGE";
	//TODO
	/*自定义命令常量*/
	public static final String VALUE_SETTING_ACT_PICTURE = "ACT_PICTURE";
	public static final String VALUE_SETTING_OBJ_PICTURE = "OBJ_PICTURE";//拍照
	
	public static final String VALUE_SETTING_OBJ_VALUMN = "OBJ_VALUMN";
	public static final String VALUE_SETTING_OBJ_LIGHT = "OBJ_LIGHT";
	public static final String VALUE_SETTING_OBJ_FLOW = "OBJ_FLOW";
	public static final String VALUE_SETTING_OBJ_RINGTONE = "OBJ_RINGTONE";
	public static final String VALUE_SETTING_OBJ_WALLPAPER = "OBJ_WALLPAPER";
	public static final String VALUE_SETTING_OBJ_FACE = "OBJ_FACE";
	public static final String VALUE_SETTING_OBJ_TIME = "OBJ_TIME";
	public static final String VALUE_SETTING_OBJ_3G = "OBJ_3G";
	public static final String VALUE_SETTING_OBJ_WIFI = "OBJ_WIFI";
	public static final String VALUE_SETTING_OBJ_WIFI_SPOT = "OBJ_WIFI_SPOT";
	public static final String VALUE_SETTING_OBJ_BLUETOOTH = "OBJ_BLUETOOTH";
	public static final String VALUE_SETTING_OBJ_GPS = "OBJ_GPS";
	public static final String VALUE_SETTING_OBJ_ROTATION = "OBJ_ROTATION";
	public static final String VALUE_SETTING_OBJ_AUTOLIGHT = "OBJ_AUTOLIGHT";
	public static final String VALUE_SETTING_OBJ_MODEL_STANDARD = "OBJ_MODEL_STANDARD";
	public static final String VALUE_SETTING_OBJ_MODEL_MUTE = "OBJ_MODEL_MUTE";
	public static final String VALUE_SETTING_OBJ_MODEL_VIBRA = "OBJ_MODEL_VIBRA";
	public static final String VALUE_SETTING_OBJ_MODEL_INAIR = "OBJ_MODEL_INAIR";
	public static final String VALUE_SETTING_OBJ_MODEL_INCAR = "OBJ_MODEL_INCAR";
	public static final String VALUE_SETTING_OBJ_MODEL_SAVEPOWER = "OBJ_MODEL_SAVEPOWER";
	public static final String VALUE_SETTING_OBJ_MODEL_SAVEFLOW = "OBJ_MODEL_SAVEFLOW";
	public static final String VALUE_SETTING_OBJ_MODEL_GUEST = "OBJ_MODEL_GUEST";
	public static final String VALUE_SETTING_OBJ_MODEL_OUTDOOR = "OBJ_MODEL_OUTDOOR";
	public static final String VALUE_SETTING_OBJ_MEMORY = "OBJ_MEMORY";
	public static final String VALUE_SETTING_OBJ_DEVICE = "OBJ_DEVICE";
	public static final String VALUE_SETTING_OBJ_CALL = "OBJ_CALL";
	public static final String VALUE_SETTING_OBJ_MUSIC_LOOP = "OBJ_MUSIC_LOOP";
	public static final String VALUE_SETTING_OBJ_MUSIC_SHUFFLE_PLAYBACK = "OBJ_MUSIC_SHUFFLE_PLAYBACK";
	public static final String VALUE_SETTING_OBJ_MUSIC_ORDER_PLAYBACK = "OBJ_MUSIC_ORDER_PLAYBACK";
	public static final String VALUE_SETTING_OBJ_MUSIC_SINGLE_CYCLE = "OBJ_MUSIC_SINGLE_CYCLE";
	public static final String VALUE_SETTING_OBJ_MUSIC_LIST_CYCLE = "OBJ_MUSIC_LIST_CYCLE";
	public static final String VALUE_SETTING_OBJ_MUSIC_FULL_CYCLE = "OBJ_MUSIC_FULL_CYCLE";
	public static final String VALUE_SETTING_OBJ_MUSIC_PREVIOUS_ITEM = "OBJ_MUSIC_PREVIOUS_ITEM";
	public static final String VALUE_SETTING_OBJ_MUSIC_NEXT_ITEM = "OBJ_MUSIC_NEXT_ITEM";
	public static final String VALUE_SETTING_OBJ_AUDIO = "OBJ_AUDIO";
	public static final String VALUE_SETTING_OBJ_NAVI = "OBJ_NAVI";
	public static final String VALUE_SETTING_OBJ_AIR_CONDITION = "OBJ_AIR_CONDITION";
	public static final String VALUE_SETTING_OBJ_INSIDE_MODEL = "OBJ_INSIDE_MODEL";
	public static final String VALUE_SETTING_OBJ_OUTSIDE_MODEL = "OBJ_OUTSIDE_MODEL";
	public static final String VALUE_SETTING_OBJ_AUTO_LOOP_MODEL = "OBJ_AUTO_LOOP_MODEL";
	public static final String VALUE_SETTING_OBJ_AIR_CONDITION_MODEL = "OBJ_AIR_CONDITION_MODEL";
	public static final String VALUE_SETTING_OBJ_NEWEST_SMS = "OBJ_NEWEST_SMS";
	public static final String VALUE_SETTING_OBJ_UNREAD_SMS = "OBJ_UNREAD_SMS";
	public static final String VALUE_SETTING_OBJ_MUSIC_BLUETOOTH = "OBJ_MUSIC_BLUETOOTH";
	public static final String VALUE_SETTING_OBJ_VOLUMN = "OBJ_VOLUMN";
	public static final String VALUE_SETTING_OBJ_SCREEN = "OBJ_SCREEN";
	public static final String VALUE_SETTING_OBJ_MUSIC = "OBJ_MUSIC";
	public static final String VALUE_SETTING_OBJ_FRONTCAMERA = "OBJ_FRONTCAMERA";
	public static final String VALUE_SETTING_OBJ_REARCAMERA = "OBJ_REARCAMERA";
	public static final String VALUE_SETTING_OBJ_PIP = "OBJ_PIP";
	public static final String VALUE_SETTING_OBJ_RADIO = "OBJ_RADIO";
	public static final String VALUE_SETTING_OBJ_CARCORDER = "OBJ_CARCORDER";
	public static final String VALUE_SETTING_OBJ_SDCARD = "OBJ_SDCARD";
	public static final String VALUE_SETTING_OBJ_VIDEO = "OBJ_VIDEO";
	public static final String VALUE_SETTING_OBJ_POWER = "OBJ_POWER";
	public static final String VALUE_SETTING_OBJ_STANDBY = "OBJ_STANDBY";
	public static final String VALUE_SETTING_OBJ_HOMEPAGE = "OBJ_HOMEPAGE";
	public static final String VALUE_SETTING_OBJ_ADDRESS_BOOK = "OBJ_ADDRESS_BOOK";
	public static final String VALUE_SETTING_OBJ_CALL_LOG = "OBJ_CALL_LOG";

	public static final String VALUE_TYPE_SOME_PERSON = "MUTIPLE_CONTACTS";
	public static final String VALUE_TYPE_INPUT_CONTACT = "INPUT_CONTACT";
	public static final String VALUE_TYPE_CALL_START = "CALL";
	public static final String VALUE_TYPE_SOME_NUMBERS = "MUTIPLE_NUMBERS";
	public static final String VALUE_TYPE_CALL_ONE_NUMBER = "CONFIRM_CALL";
	public static final String VALUE_TYPE_CALL_OK = "CALL_OK";
	public static final String VALUE_TYPE_CALL_CANCEL = "CALL_CANCEL";
	public static final String VALUE_TYPE_INPUT_MSG_CONTENT = "INPUT_FREETEXT_SMS";

	public static final String VALUE_UI_PROTOCAL_SHOW = "UI_PROTOCAL_SHOW";

	/* KW设置命令 */
	public static final String VALUE_SETTING_ACT_OPEN_DESK_LYRIC = "ACT_OPEN_LYRIC";
	public static final String VALUE_SETTING_ACT_CLOSE_DESK_LYRIC = "ACT_CLOSE_LYRIC";
	public static final String ACTION_MUSIC_START_KWAPP = "ACTION_MUSIC_START_KWAPP";
	/* KW设置命令 */

	// origin type - domain
	public static final String DOMAIN_ALARM = "cn.yunzhisheng.alarm";
	public static final String DOMAIN_APP = "cn.yunzhisheng.appmgr";
	public static final String DOMAIN_CALL = "cn.yunzhisheng.call";
	public static final String DOMAIN_CONTACT = "cn.yunzhisheng.contact";
	public static final String DOMAIN_CONTACT_SEND = "cn.yunzhisheng.contact";
	public static final String DOMAIN_FLIGHT = "cn.yunzhisheng.flight";
	public static final String DOMAIN_INCITY_SEARCH = "cn.yunzhisheng.localsearch";
	public static final String DOMAIN_MUSIC = "cn.yunzhisheng.music";
	public static final String DOMAIN_NEARBY_SEARCH = "cn.yunzhisheng.localsearch";
	public static final String DOMAIN_NOTE = "cn.yunzhisheng.note";
	public static final String DOMAIN_POSITION = "cn.yunzhisheng.map";
	public static final String DOMAIN_ROUTE = "cn.yunzhisheng.map";
	public static final String DOMAIN_REMINDER = "cn.yunzhisheng.reminder";
	public static final String DOMAIN_SEARCH = "cn.yunzhisheng.websearch";
	public static final String DOMAIN_SITEMAP = "cn.yunzhisheng.website";
	public static final String DOMAIN_SMS = "cn.yunzhisheng.sms";
	public static final String DOMAIN_STOCK = "cn.yunzhisheng.stock";
	public static final String DOMAIN_TRAIN = "cn.yunzhisheng.train";
	public static final String DOMAIN_TV = "cn.yunzhisheng.tv";
	public static final String DOMAIN_WEATHER = "cn.yunzhisheng.weather";
	public static final String DOMAIN_YELLOWPAGE = "cn.yunzhisheng.hotline";
	public static final String DOMAIN_WEBSEARCH = "cn.yunzhisheng.websearch";
	public static final String DOMAIN_CALENDAR = "cn.yunzhisheng.calendar";
	public static final String DOMAIN_NEWS = "cn.yunzhisheng.news";
	public static final String DOMAIN_COOKBOOK = "cn.yunzhisheng.cookbook";
	public static final String DOMAIN_TRANSLATION = "cn.yunzhisheng.translation";
	public static final String DOMAIN_DIANPING = "cn.yunzhisheng.localsearch";
	public static final String DOMAIN_SETTING = "cn.yunzhisheng.setting";
	public static final String DOMAIN_MOVIE = "cn.yunzhisheng.movie";
	public static final String DOMAIN_NOVEL = "cn.yunzhisheng.novel";
	public static final String DOMAIN_VIDEO = "cn.yunzhisheng.video";
	public static final String DOMAIN_CHAT = "cn.yunzhisheng.chat";
	public static final String DOMAIN_BROADCAST = "cn.yunzhisheng.broadcast";

	public static final String DOMAIN_ERROR = "DOMAIN_ERROR";
	public static final String DOMAIN_LOCAL = "DOMAIN_LOCAL";

	public static final String DOMAIN_CHANNEL_SWITCH = "cn.yunzhisheng.setting.tv";
	public static final String DOMAIN_SHOP = "cn.yunzhisheng.shopping";
	public static final String DOMAIN_MOBILE_CONTROL = "DOMAIN_MOBILE_CONTROL";

	public static final String DOMAIN_CODE_FAVORITE_ROUTE = "FAVORITE_ROUTE";

	// memo
	public static final String KEY_TEXT = "text";
	public static final String KEY_MORNING = "am";
	public static final String KEY_AFTERNOON = "pm";

	public static final String KEY_YEAR_REPEAT = "yearrep";
	public static final String KEY_MONTH_REPEAT = "monthrep";
	public static final String KEY_WEEK_REPEAT = "weekrep";
	public static final String KEY_YEAR = "yy";
	public static final String KEY_MONTH = "MM";
	public static final String KEY_DAY = "dd";
	public static final String KEY_HOUR = "hh";
	public static final String KEY_MINUTE = "mm";
	public static final String KEY_WEEKDAY = "ww";

	public static final String KEY_YEAR_STEP = "year";
	public static final String KEY_MON_STEP = "month";
	public static final String KEY_DAY_STEP = "day";
	public static final String KEY_HOUR_STEP = "hour";
	public static final String KEY_MIN_STEP = "minute";
	public static final String KEY_WEEK_STEP = "week";

	public static final String KEY_REPEATDATE = "repeatDate";
	public static final String KEY_DATETIME = "dateTime";
	public static final String KEY_LABEL = "label";

	/********************** session message *********************/
	public static final int MESSAGE_START_TALK = 1001;
	public static final int MESSAGE_STOP_TALK = 1002;
	public static final int MESSAGE_CANCEL_TALK = 1003;
	public static final int MESSAGE_SESSION_DONE = 1004;
	public static final int MESSAGE_SESSION_CANCEL = 1005;
	public static final int MESSAGE_DISABLE_MICROPHONE = 1006;
	public static final int MESSAGE_ENABLE_MICROPHONE = 1007;
	public static final int MESSAGE_REQUEST_PLAY_TTS = 1009;
	public static final int MESSAGE_REQUEST_CANCEL_TTS = 1010;
	public static final int MESSAGE_NEW_PROTOCAL = 1011;

	public static final int MESSAGE_VAD_RECORD_STOP = 1012;

	public static final int MESSAGE_ADD_ANSWER_VIEW = 1013;
	public static final int MESSAGE_ADD_ANSWER_TEXT = 1014;
	public static final int MESSAGE_ADD_QUESTION_TEXT = 1015;
	public static final int MESSAGE_UI_OPERATE_PROTOCAL = 1016;
	public static final int MESSAGE_TASK_DELY = 1017;
	public static final int MESSAGE_OPERATION_TIMEOUT = 1018;
	public static final int MESSAGE_REQUEST_RESET_TIMER = 1019;
	public static final int MESSAGE_REQUEST_CANCEL_TIMER = 1020;
	public static final int MESSAGE_DISMISS_WINDOW = 1021;

	public static final int MESSAGE_SHOW_HELP_VIEW = 1022;
	public static final int MESSAGE_START_WAKEUP = 1023;
	public static final int MESSAGE_STOP_WAKEUP = 1024;
	public static final int MESSAGE_PLAY_REVEIVED_MESSAGE = 1025;
	public static final int MESSAGE_PLAY_BEEP_SOUND = 1026;
	public static final int MESSAGE_ANSWER_CALL = 1027;
	public static final int MESSAGE_MOREFUNCTION_PROTOCAL = 40001;

	public static String getMessageName(int what) {
		switch (what) {
		case MESSAGE_START_TALK:
			return "MESSAGE_START_TALK";
		case MESSAGE_STOP_TALK:
			return "MESSAGE_STOP_TALK";
		case MESSAGE_CANCEL_TALK:
			return "MESSAGE_CANCEL_TALK";
		case MESSAGE_SESSION_DONE:
			return "MESSAGE_SESSION_DONE";
		case MESSAGE_SESSION_CANCEL:
			return "MESSAGE_SESSION_CANCEL";
		case MESSAGE_DISABLE_MICROPHONE:
			return "MESSAGE_DISABLE_MICROPHONE";
		case MESSAGE_ENABLE_MICROPHONE:
			return "MESSAGE_ENABLE_MICROPHONE";
		case MESSAGE_REQUEST_PLAY_TTS:
			return "MESSAGE_REQUEST_PLAY_TTS";
		case MESSAGE_REQUEST_CANCEL_TTS:
			return "MESSAGE_REQUEST_CANCEL_TTS";
		case MESSAGE_NEW_PROTOCAL:
			return "MESSAGE_NEW_PROTOCAL";
		case MESSAGE_VAD_RECORD_STOP:
			return "MESSAGE_VAD_RECORD_STOP";
		case MESSAGE_ADD_ANSWER_VIEW:
			return "MESSAGE_ADD_ANSWER_VIEW";
		case MESSAGE_ADD_ANSWER_TEXT:
			return "MESSAGE_ADD_ANSWER_TEXT";
		case MESSAGE_ADD_QUESTION_TEXT:
			return "MESSAGE_ADD_QUESTION_TEXT";
		case MESSAGE_UI_OPERATE_PROTOCAL:
			return "MESSAGE_UI_OPERATE_PROTOCAL";
		case MESSAGE_TASK_DELY:
			return "MESSAGE_TASK_DELY";
		case MESSAGE_OPERATION_TIMEOUT:
			return "MESSAGE_OPERATION_TIMEOUT";
		case MESSAGE_REQUEST_RESET_TIMER:
			return "MESSAGE_REQUEST_RESET_TIMER";
		case MESSAGE_REQUEST_CANCEL_TIMER:
			return "MESSAGE_REQUEST_CANCEL_TIMER";
		case MESSAGE_DISMISS_WINDOW:
			return "MESSAGE_DISMISS_WINDOW";
		case MESSAGE_SHOW_HELP_VIEW:
			return "MESSAGE_SHOW_HELP_VIEW";
		case MESSAGE_START_WAKEUP:
			return "MESSAGE_START_WAKEUP";
		case MESSAGE_STOP_WAKEUP:
			return "MESSAGE_STOP_WAKEUP";
		case MESSAGE_PLAY_REVEIVED_MESSAGE:
			return "MESSAGE_PLAY_REVEIVED_MESSAGE";
		case MESSAGE_MOREFUNCTION_PROTOCAL:
			return "MESSAGE_MOREFUNCTION_PROTOCAL";
		}

		return String.valueOf(what);
	}
}
