package com.app.empire.world.request;

import com.app.session.Session;

public class GetGPSServerInfoRequest extends SessionRequest {

	public GetGPSServerInfoRequest(int id, int sessionId, Session session) {
		super(IRequestType.GET_GPS_INFO, id, sessionId, session);
	}

}