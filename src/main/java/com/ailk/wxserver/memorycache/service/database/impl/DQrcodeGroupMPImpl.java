package com.ailk.wxserver.memorycache.service.database.impl;

import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.lcims.support.util.StringUtils;
import com.ailk.wxserver.memorycache.service.database.interfaces.DQrcodeGroupMP;
import com.ailk.wxserver.po.DQrcodeGroup;
import com.sybase.jdbc3.a.b.l;

public class DQrcodeGroupMPImpl extends AbstractMP<DQrcodeGroup> implements DQrcodeGroupMP {

	@Override
	public List<DQrcodeGroup> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("dQrcodeGroup", new GenericTypeReference<List<DQrcodeGroup>>(){});
	}

	@Override
	public DQrcodeGroup getQrcodeGroup(String scene, String eccode) {
		if(StringUtils.isEmpty(scene)){
			return null;
		}
		if(StringUtils.isEmpty(eccode)){
			return null;
		}
		List<DQrcodeGroup> list = getAll();
		for(DQrcodeGroup dQrcodeGroup:list){
			if(eccode.equals(dQrcodeGroup.getEccode()) && scene.equals(dQrcodeGroup.getScene())){
				return dQrcodeGroup;
			}
		}
		return null;
	}

}
