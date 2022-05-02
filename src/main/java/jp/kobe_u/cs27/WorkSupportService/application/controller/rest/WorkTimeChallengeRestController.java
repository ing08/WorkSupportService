package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkTimeChallengeForm;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeChallengeDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkTimeChallenge;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkTimeChallengeService;

/**
 * 目標作業時間を操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkTimeChallengeRestController {
    /** 目標作業時間のサービス */
    private final WorkTimeChallengeService wtcs;

    /* -------------------- Create -------------------- */

    /**
     * 目標作業時間を設定
     *
     * @param uid
     * @param WorkTimeChallengeForm
     * @return WorkTimeChallenge
     */
    @PostMapping("/challenge/{uid}")
    public WorkTimeChallenge addWorkTimeChallenge(@PathVariable String uid,
            @RequestBody @Validated WorkTimeChallengeForm form) {
        return wtcs.createWorkTimeChallenge(uid, form.getUntilst(), form.getHour() * 3600);
    }

    /* -------------------- Read -------------------- */

    /**
     * 直近の目標作業時間を所得
     *
     * @param uid
     * @return WorkTimeChallengeDto
     */
    @GetMapping("/challenge/{uid}")
    public WorkTimeChallengeDto getNewestWorkTimeChallengeByUid(@PathVariable String uid) {
        return wtcs.getWorkTimeChallenge(uid);
    }

    /* -------------------- Delete -------------------- */

    /**
     * 直近の目標作業時間を削除
     *
     * @param uid
     * @return true
     */
    @DeleteMapping("/challenge/{uid}")
    public Boolean deleteNewsetWorkTimeChallengeByUid(@PathVariable String uid) {
        wtcs.deleteWorkTimeChallenge(uid, wtcs.getWorkTimeChallenge(uid).getWtcid());

        return true;
    }
}