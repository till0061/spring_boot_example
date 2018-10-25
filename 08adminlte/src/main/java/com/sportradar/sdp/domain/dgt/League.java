package com.sportradar.sdp.domain.dgt;

import com.sportradar.sdp.domain.common.BaseLeague;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Table;

@Slf4j
@Data
@Entity
@Table(name = "League")
public class League extends BaseLeague {
	@Override
	public String getExpressId() {
		return null;
	}

	@Override
	public String getIdXRefs() {
		return null;
	}

	@Override
	public void setMergedIdXRefs(String mergedIdXRefs) {

	}
}
