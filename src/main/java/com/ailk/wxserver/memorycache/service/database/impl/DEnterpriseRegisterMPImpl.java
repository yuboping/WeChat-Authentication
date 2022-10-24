package com.ailk.wxserver.memorycache.service.database.impl;

import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.wxserver.memorycache.service.database.interfaces.DEnterpriseRegisterMP;
import com.ailk.wxserver.po.DEnterpriseRegister;

/**
 * 
 * @author dengliugao
 *
 */
public class DEnterpriseRegisterMPImpl extends AbstractMP<DEnterpriseRegister> implements DEnterpriseRegisterMP{
	
	@Override
	public List<DEnterpriseRegister> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("dEnterpriseRegister", new GenericTypeReference<List<DEnterpriseRegister>>(){});
	}

	@Override
	public DEnterpriseRegister getByRegisterid(String registerid) {
		if(registerid == null){
			return null;
		}
		List<DEnterpriseRegister> list = getAll();
		for (DEnterpriseRegister der : list) {
			if (registerid.equals(der.getRegisterid())) {
				return der;
			}
		}
		return null;
	}

	@Override
	public DEnterpriseRegister getByUncode(String uncode) {
		if(uncode == null){
			return null;
		}
		List<DEnterpriseRegister> list = getAll();
		for (DEnterpriseRegister der : list) {
			if (uncode.equals(der.getUncode())) {
				return der;
			}
		}
		return null;
	}

	@Override
	public DEnterpriseRegister getByEccode(String eccode) {
		if(eccode == null){
			return null;
		}
		List<DEnterpriseRegister> list = getAll();
		for (DEnterpriseRegister der : list) {
			if (eccode.equals(der.getEccode())) {
				return der;
			}
		}
		return null;
	}
	
	
}
