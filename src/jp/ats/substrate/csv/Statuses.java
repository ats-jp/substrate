package jp.ats.substrate.csv;

/**
 * @author 千葉 哲嗣
 */
class Statuses {

	/**
	 * 「解析開始」状態
	 */
	static final Status START = new StartStatus();

	/**
	 * 「ダブルクォート開始位置」状態
	 */
	private static final Status START_QUOTE = new StartQuoteStatus();

	/**
	 * 「ダブルクォート終了もしくはダブルクォート文字のエスケープ時」状態
	 */
	private static final Status END_QUOTE_OR_ESCAPE = new ReserveQuoteStatus();

	/**
	 * 「ダブルクォート内項目本体」状態
	 */
	private static final Status QUOTE = new QuoteStatus();

	/**
	 * 「カンマ到達」状態
	 */
	private static final Status COMMA = new CommaStatus();

	/**
	 * 「ダブルクォートなし項目本体」状態
	 */
	private static final Status BODY = new BodyStatus();

	/**
	 * 「改行CR到達」状態
	 */
	private static final Status CR = new CRStatus();

	/**
	 * 「改行LF到達」状態
	 */
	private static final Status LF = new LFStatus();

	private static class StartStatus extends Status {

		private StartStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				listener.receiveStartColumn();
				return START_QUOTE;
			case ',':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				return COMMA;
			case '\r':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				return CR;
			case '\n':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				listener.receiveNewLine("\n");
				return LF;
			default:
				listener.receiveStartColumn();
				listener.receiveColumnBody(c);
				return BODY;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveEndCSV();
		}
	}

	static class StartQuoteStatus extends Status {

		private StartQuoteStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				return END_QUOTE_OR_ESCAPE;
			default:
				listener.receiveColumnBody(c);
				return QUOTE;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveError("終端の \" がありません");
		}
	}

	static class ReserveQuoteStatus extends Status {

		private ReserveQuoteStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				listener.receiveColumnBody(c);
				return QUOTE;
			case ',':
				listener.receiveEndColumn();
				return COMMA;
			case '\r':
				listener.receiveEndColumn();
				return CR;
			case '\n':
				listener.receiveEndColumn();
				listener.receiveNewLine("\n");
				return LF;
			default:
				listener.receiveError("終端の \" 以降に文字があります");
				return this;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveEndColumn();
			listener.receiveEndCSV();
		}
	}

	static class QuoteStatus extends Status {

		private QuoteStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				return END_QUOTE_OR_ESCAPE;
			default:
				listener.receiveColumnBody(c);
				return this;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveError("終端の \" がありません");
		}
	}

	static class CommaStatus extends Status {

		private CommaStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				listener.receiveStartColumn();
				return START_QUOTE;
			case ',':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				return COMMA;
			case '\r':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				return CR;
			case '\n':
				listener.receiveStartColumn();
				listener.receiveEndColumn();
				listener.receiveNewLine("\n");
				return LF;
			default:
				listener.receiveStartColumn();
				listener.receiveColumnBody(c);
				return BODY;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveStartColumn();
			listener.receiveEndColumn();
			listener.receiveEndCSV();
		}
	}

	static class BodyStatus extends Status {

		private BodyStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '"':
				listener.receiveError("\"\" の外では \" を含めることはできません");
				return this;
			case ',':
				listener.receiveEndColumn();
				return COMMA;
			case '\r':
				listener.receiveEndColumn();
				return CR;
			case '\n':
				listener.receiveEndColumn();
				listener.receiveNewLine("\n");
				return LF;
			default:
				listener.receiveColumnBody(c);
				return BODY;
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveEndColumn();
			listener.receiveEndCSV();
		}
	}

	static class CRStatus extends Status {

		private CRStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			switch (c) {
			case '\n':
				listener.receiveNewLine("\r\n");
				return LF;
			default:
				listener.receiveNewLine("\r");
				return START.next(c, listener);
			}
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveEndColumn();
			listener.receiveEndCSV();
		}
	}

	static class LFStatus extends Status {

		private LFStatus() {}

		@Override
		Status next(char c, CSVListener listener) {
			return START.next(c, listener);
		}

		@Override
		void end(CSVListener listener) {
			listener.receiveEndColumn();
			listener.receiveEndCSV();
		}
	}
}
