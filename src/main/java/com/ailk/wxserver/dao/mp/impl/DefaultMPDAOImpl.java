package com.ailk.wxserver.dao.mp.impl;

import java.util.List;

public class DefaultMPDAOImpl extends MyBaseMPDAOImpl{


	@Override
	public List<?> getAll() throws Exception {
		return getAllOrderby(null);
	}


}
