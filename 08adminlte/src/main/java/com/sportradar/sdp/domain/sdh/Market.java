package com.sportradar.sdp.domain.sdh;

import com.sportradar.sdp.domain.common.BaseMarket;
import com.sportradar.sdp.domain.dgt.League;
import lombok.Data;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "Market")
@Data
public class Market extends BaseMarket {
	/*
	@Transient
	private String brMarketIdXRefs;

	@Transient
	private String dgtMarketIdXRefs;

*/
	@Transient
	private List<com.sportradar.sdp.domain.dgt.Market> dgtMarketXRefs;

	@Transient
	private List<com.sportradar.sdp.domain.br.Market> brMarketXRefs;

	@OneToMany(fetch= FetchType.LAZY, mappedBy="marketId", cascade = {CascadeType.PERSIST})
	private List<MarketLanguage> languages;

	@Override
	public String getExpressId() {
		return this.getMarketId().toString();
	}

	@Override
	public String getIdXRefs() {
		return this.getMarketIdXRefs();
	}

	@Override
	public void setMergedIdXRefs(String mergedIdXRefs) {
		this.setMarketIdXRefs(mergedIdXRefs);
	}

	public String getLangString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		//Make sure Fetch executing
		List<MarketLanguage> langs = this.getLanguages();

		if (null != langs && !langs.isEmpty()) {
			Iterator<MarketLanguage> it = langs.iterator();

			while(it.hasNext()) {
				MarketLanguage sl = it.next();
				sb.append(sl.getLanguageCode());
				sb.append(":");
				sb.append(sl.getMarketName());
				if (it.hasNext()) {
					sb.append(",");
				}
			}
		}

		sb.append("]");

		return sb.toString();
	}
}