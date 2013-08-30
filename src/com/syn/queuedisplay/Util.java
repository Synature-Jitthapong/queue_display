package com.syn.queuedisplay;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j1tth4.mobile.util.JSONUtil;
import com.syn.mpos.model.QueueDisplayInfo;
import com.syn.mpos.model.WebServiceResult;

public class Util {
	public static List<TakeAwayData> toTakeAwayObj(String json)
			throws Exception {
		List<TakeAwayData> takeAwayLst = new ArrayList<TakeAwayData>();

		JSONUtil jsonUtil = new JSONUtil();
		Type type = new TypeToken<WebServiceResult>() {
		}.getType();

		WebServiceResult wsResult = (WebServiceResult) jsonUtil.toObject(type,
				json);

		if (wsResult.getiResultID() == 0) {

			jsonUtil = new JSONUtil();
			type = new TypeToken<List<TakeAwayData>>() {
			}.getType();

			takeAwayLst = (List<TakeAwayData>) jsonUtil.toObject(type,
					wsResult.getSzResultData());

		}
		return takeAwayLst;
	}

	public static QueueDisplayInfo toQueueDisplayObj(String json)
			throws Exception {
		QueueDisplayInfo queueDisplayInfo = new QueueDisplayInfo();

		JSONUtil jsonUtil = new JSONUtil();
		Type type = new TypeToken<QueueDisplayInfo>() {
		}.getType();

		queueDisplayInfo = (QueueDisplayInfo) jsonUtil.toObject(type, json);

		return queueDisplayInfo;
	}
}
