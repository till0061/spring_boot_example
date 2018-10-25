package com.sportradar.sdp.domain.sdh;

import com.sportradar.sdp.domain.common.BaseRegion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Data
@Slf4j
@Entity
@Table(name = "Region")
public class Region extends BaseRegion {
	/*
	@Transient
	private String brRegionIdXRefs;

	@Transient
	private String dgtRegionIdXRefs;
	*/

	@Transient
	private List<com.sportradar.sdp.domain.dgt.Region> dgtRegionXRefs;

	@Transient
	private List<com.sportradar.sdp.domain.br.Region> brRegionXRefs;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="regionNum", cascade = {CascadeType.PERSIST})
	private List<RegionLanguage> languages;

	@Override
	public String getExpressId() {
		return this.getRegionNum().toString();
	}

	@Override
	public String getIdXRefs() {
		return this.getRegionNumXRefs();
	}

	@Override
	public void setMergedIdXRefs(String mergedIdXRefs) {
		this.setRegionNumXRefs(mergedIdXRefs);
	}

	public String getLangString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		//Make sure Fetch executing
		List<RegionLanguage> langs = this.getLanguages();

		if (null != langs && !langs.isEmpty()) {
			Iterator<RegionLanguage> it = langs.iterator();

			while(it.hasNext()) {
				RegionLanguage sl = it.next();
				sb.append(sl.getLanguageCode());
				sb.append(":");
				sb.append(sl.getRegionName());
				if (it.hasNext()) {
					sb.append(",");
				}
			}
		}

		sb.append("]");

		return sb.toString();
	}
}