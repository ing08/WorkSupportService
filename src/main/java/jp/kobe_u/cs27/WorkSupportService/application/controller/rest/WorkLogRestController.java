package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.application.form.LidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkLogForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.QueryForm;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkLog;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkLogService;

/**
 * 作業ログを操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkLogRestController {
    /** 作業ログのサービス */
    private final WorkLogService wls;

    /* -------------------- Create -------------------- */

    /**
     * 作業ログを手動で追加
     *
     * @param uid
     * @param WorkLogForm
     * @return WorkLog
     */
    @PostMapping("/worklogs/{uid}/")
    public WorkLog createWorkLogManually(@PathVariable String uid, @RequestBody @Validated WorkLogForm form) {
        return wls.createWorkLogManually(uid, form.getWid(), form.getDatesince(), form.getTimesince(),
                form.getDateuntil(), form.getTimeuntil());
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザに紐づくすべての作業ログを作業名付きで所得
     *
     * @param uid
     * @return List<WorkLogWithWnameDto>
     */
    @GetMapping("/worklogs/{uid}")
    public List<WorkLogWithWnameDto> getAllWorkLogByUid(@PathVariable String uid) {
        return wls.getAllWorkLogByUid(uid);
    }

    // /**
    // * ユーザの作業ログを期間を指定して所得
    // *
    // * @param uid
    // * @param QueryForm
    // * @return List<WorkLogWithWnameDto>
    // */
    // @GetMapping("/worklogs/{uid}/query")
    // public List<WorkLogWithWnameDto> getWorkLogSettingPeriod(@PathVariable String
    // uid,
    // @RequestBody @Validated QueryForm form) {
    // return wls.getWorkLogSettingPeriod(uid, form.getSince(), form.getUntil());
    // }

    /**
     * 期間をを指定して作業ごとの作業時間を計算
     *
     * @param uid
     * @param QueryForm
     * @return List<WorkTimeWithWnameDto>
     */
    @GetMapping("worklogs/{uid}/query/worktime")
    public List<WorkTimeWithWnameDto> getWorkTimeSettingPeriod(@PathVariable String uid,
            @RequestBody @Validated QueryForm form) {
        return wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, form.getSince(), form.getUntil()));
    }

    /**
     * ユーザの作業ログを作業と期間を指定して所得
     *
     * @param uid
     * @param wid
     * @param QueryForm
     * @return List<WorkLogWithWnameDto>
     */
    @GetMapping("/worklogs/{uid}/query/{wid}")
    public List<WorkLogWithWnameDto> getWorkLogSettingPeriodAndWid(@PathVariable String uid, @PathVariable String wid,
            @RequestBody @Validated QueryForm form) {
        String since = form.getSince();

        if (since.equals("all")) { // 全期間対象なら
            if (wid.equals("all")) { // 全作業対象なら
                return wls.getAllWorkLogByUid(uid);
            } else if (wid.equals("unspecified")) { // 未指定対象なら
                // 削除済みを表示
            }

            return wls.getWorkLogByWid(uid, wid);
        } else {
            if (wid.equals("all")) { // 全作業対象なら
                return wls.getWorkLogSettingPeriod(uid, form.getSince(), form.getUntil());
            } else if (wid.equals("unspecified")) { // 未指定対象なら
                // 削除済みを表示
            }

            return wls.getWorkLogByWidSettingPeriod(uid, wid, form.getSince(), form.getUntil());
        }
    }

    /**
     * 全期間の総合作業時間ランキングを所得
     *
     * @return List<Entry<String, Integer>>
     */
    @GetMapping("/ranking/all")
    public List<Entry<String, Integer>> getWorkTimeRanking() {
        return wls.getAllWorkTimeRanking();
    }

    /**
     * 指定期間の総合作業時間ランキングを所得
     *
     * @param QueryForm
     * @return List<Entry<String, Integer>>
     */
    @GetMapping("/ranking/query")
    public List<Entry<String, Integer>> getWorkTimeRankingSettingPeriod(@RequestBody QueryForm form) {
        return wls.getWorkTimeRankingSettingPeriod(form.getSince(), form.getUntil());
    }

    /**
     * 昨日のランキング結果をミクちゃんに読み上げさせる
     */
    @GetMapping("/ranking/yesterday")
    public void getYesterdayLongestWorkTimeUser() {
        wls.getYesterdayLongestWorkTimeUser();
    };

    /* -------------------- Delete -------------------- */

    /**
     * 作業ログを削除
     *
     * @param uid
     * @param LidForm
     */
    @DeleteMapping("/worklogs/{uid}")

    public Boolean deleteWorkLog(@PathVariable String uid, @RequestBody @Validated LidForm form) {
        wls.deleteWorkLog(uid, form.getLid()); // 作業ログを削除

        return true;
    }
}
