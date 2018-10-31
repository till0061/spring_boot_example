package com.sportradar.sdh.ctrl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sportradar.sdh.dao.sdp.*;
import com.sportradar.sdh.domain.sdp.League;
import com.sportradar.sdh.domain.sdp.Region;
import com.sportradar.sdh.dto.dts.DataTablesInput;
import com.sportradar.sdh.dto.dts.DataTablesOutput;
import com.sportradar.sdh.dto.sdp.LeagueDto;
import com.sportradar.sdh.dto.sdp.RegionDto;
import com.sportradar.sdh.dto.sdp.SportDto;
import com.sportradar.sdh.dto.sdp.Translation;
import com.sportradar.sdh.dto.system.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/league/*")
public class LeagueCtrl {
	private static final String prefix = "league";
	@Autowired
	private SdpLeagueDao sdpLeagueDao;

	@Autowired
	private SdpLeagueGroupDao sdpLeagueGroupDao;

	@Autowired
	private SdpRegionDao sdpRegionDao;

	@Autowired
	private SdpSportDao sdpSportDao;


	private SdpLanguageDao sdpLanguageDao;

	@GetMapping("/pair")
	public String index() {
		return prefix+"/pairIndex";
	}

	@GetMapping("/i18n")
	public String i18nIndex() {
		return prefix+"/i18nIndex";
	}

	@GetMapping("/data")
	public String dataIndex() {
		return prefix+"/dataIndex";
	}

	@GetMapping("/findAll")
	@ResponseBody
	public DataTablesOutput<LeagueDto> findAll(@Valid DataTablesInput input) {
		//DataTablesOutput<MarketOption> ds = this.sdhMarketOptionDao.findAll(input);

		PageHelper.startPage((input.getStart() / input.getLength()) +1 , input.getLength());

		Page<League> page = this.sdpLeagueDao.findByPage();
		DataTablesOutput<LeagueDto> ds = new DataTablesOutput<LeagueDto>();
		ds.setData(coverDto(page.getResult()));
		ds.setDraw(input.getDraw());
		ds.setRecordsFiltered(page.getTotal());
		ds.setRecordsTotal(page.getTotal());

		return  ds;
	}

	private List<LeagueDto> coverDto(List<League> leagues) {
		List<LeagueDto> result = new ArrayList<>();
		for (League league : leagues) {
			LeagueDto sd = new LeagueDto();

			BeanUtils.copyProperties(league, sd);

			for (League translatedRegion :this.sdpLeagueDao.findByIdWithLanguage(sd.getLeagueId())) {
				Translation translation = new Translation();

				translation.setLanguageCode(translatedRegion.getLanguage().getLanguageCode());
				translation.setLanguageName(translatedRegion.getLanguage().getLanguageName());
				translation.setTranslationValue(translatedRegion.getLeagueName());

				sd.getTranslations().add(translation);
			}
			result.add(sd);
		}
		return result;
	}

	private void saveDbI18N(LeagueDto league) {
		league.setUpdatedTime(new Date());
		League language = this.sdpLeagueDao.findByIdAndLanguageCodeWithLanguage(league.getLeagueId(), league.getLanguage().getLanguageCode());
		if (language == null) {
			this.sdpLeagueDao.insertI18N(league);
		} else {
			this.sdpLeagueDao.updateI18N(league);
		}
	}

	private void saveDbData(LeagueDto league) {
		Integer count = this.sdpLeagueDao.countById(league.getLeagueId());
		Date now = new Date();

		if (count == 0) {
			league.setLeagueId(this.sdpLeagueDao.findNextId());
			league.setCreatedTime(now);
			league.setUpdatedTime(now);
			this.sdpLeagueDao.insertData(league);
		} else {
			league.setUpdatedTime(now);
			this.sdpLeagueDao.updateData(league);
		}
	}

	@GetMapping("/pair/{id}")
	public String pair(@PathVariable Long id, Model model) {
		League sport = this.sdpLeagueDao.findById(id);
		model.addAttribute("league", sport);
		return prefix+"/pair";
	}

	@GetMapping("/i18n/{id}")
	public String i18n(@PathVariable Long id, Model model) {

		League league = this.sdpLeagueDao.findById(id);
		List<League> leagues = this.sdpLeagueDao.findByIdWithAllLanguage(id);

		model.addAttribute("league", league);
		model.addAttribute("leagues", leagues);
		return prefix+"/i18n";
	}

	@PostMapping("/i18n/save")
	@ResponseBody
	public ApiResult saveI18n(LeagueDto league, Model model) {
		log.info("Find League [{}] - [{}]",league.getLeagueId(), league.getLanguage().getLanguageCode());
		this.saveDbI18N(league);
		model.addAttribute("successFlash", "Success!");

		ApiResult apiResult = new ApiResult();
		apiResult.setStatus(HttpStatus.OK);
		apiResult.setMessage("Save Success!");
		return apiResult;
	}

	@GetMapping("/data/{id}")
	public String data(@PathVariable Long id, Model model) {
		League league = this.sdpLeagueDao.findById(id);

		if (null == league) {
			league = new League();
		}

		model.addAttribute("league", league);

		/* 因RegionSport是由 Sport 与 Region连动，所以第一次时仅找出
		* 全部的Sport，单一的Region，之后再根据选取的Sport找出可用的Region.
		* */

		List<Region> regions = new ArrayList<>();
		if (null != league.getRegionNum()) {
			Region region = this.sdpRegionDao.findById(league.getRegionNum());
			if (null != region) {
				regions.add(region);
			}
		}

		model.addAttribute("leagueGroups", this.sdpLeagueGroupDao.findAll());
		model.addAttribute("sports", this.sdpSportDao.findAll());
		model.addAttribute("regions", regions);

		return prefix+"/data";
	}
	
	@PostMapping("/data/save")
	public String saveData(LeagueDto league,Model model) {
		this.saveDbData(league);
		model.addAttribute("successFlash", "Success!");
		return prefix+"/dataIndex";
	}
	/*
	@GetMapping("/findAll")
	@ResponseBody
	public DataTablesOutput<League> findAll(@Valid DataTablesInput input) {
		DataTablesOutput<League> ds = this.sdpLeagueDao.findAll(input);
		return  ds;
	}

	@GetMapping("/")
	public String index() {
		return prefix+"/pairIndex";
	}

	@GetMapping("/pair/{id}")
	public String pair(@PathVariable Long id, Model model) {
		this.findById(id, model);

		return prefix+"/pair";
	}

	@PostMapping("/savePair")
	public String savePair(Model model) {
		model.addAttribute("successFlash", "Success!");
		return prefix+"/pairIndex";
	}

	@PostMapping("/saveI18n")
	public String saveI18n(Model model) {
		model.addAttribute("successFlash", "Success!");
		return prefix+"/i18nIndex";
	}

	@PostMapping("/saveData")
	public String saveData(Model model) {
		model.addAttribute("successFlash", "Success!");
		return prefix+"/dataIndex";
	}

	@GetMapping("/i18nIndex")
	public String i18nIndex() {
		return prefix+"/i18nIndex";
	}

	@GetMapping("/i18n/{id}")
	public String i18n(@PathVariable Long id, Model model) {

		League league = this.findById(id, model);

		this.addLanguages(model);

		this.addUsedLanguageCodes(league, model);

		return prefix+"/i18n";
	}

	@GetMapping("/dataIndex")
	public String dataIndex() {
		return prefix+"/dataIndex";
	}

	@GetMapping("/data/{id}")
	public String data(@PathVariable Long id, Model model) {
		this.findById(id, model);

		return prefix+"/data";
	}


	private League findById(Long id, Model model) {
		Optional<League> optionalRegion = this.sdpLeagueDao.findById(id);
		League league = new League();
		if (optionalRegion.isPresent()) {
			league = optionalRegion.get();
		}

		model.addAttribute("league", league);

		return league;
	}

	private void addLanguages(Model model) {
		List<Language> languageCodes = new ArrayList<>();
		languageCodes.add(new Language(1,"English"));
		languageCodes.add(new Language(512,"中文"));

		model.addAttribute("languageCodes", languageCodes);
	}

	private void addUsedLanguageCodes(League league, Model model) {
		List<Integer> usedCodes = new ArrayList<>();
		if (null != league.getLanguages()) {
			for (LeagueLanguage rl : league.getLanguages()) {
				usedCodes.add(rl.getLanguageCode());
			}
		}

		model.addAttribute("usedCodes", usedCodes);
	}


*/
}
