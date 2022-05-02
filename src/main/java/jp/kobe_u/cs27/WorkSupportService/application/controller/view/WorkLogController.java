package jp.kobe_u.cs27.WorkSupportService.application.controller.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.WorkSupportService.application.form.AchivementForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CommentForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.LidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.MeetingLogForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.QueryForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkLogForm;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.OtherValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkLogValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.LoginLog;
import jp.kobe_u.cs27.WorkSupportService.domain.service.CommentService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.LoginLogService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.UserService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkLogService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkService;

/**
 * 作業ログのコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class WorkLogController {
    /** 作業ログのサービス */
    private final WorkLogService wls;
    /** 作業のサービス */
    private final WorkService ws;
    /** コメントのサービス */
    private final CommentService cs;
    /** ユーザのサービス */
    private final UserService us;
    /** ログインログのサービス */
    private final LoginLogService lls;

    // /**
    // * 作業ログ編集ページを渡す
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return lookBack
    // */
    // @GetMapping("/{uid}/logs/lookback/")
    // public String showLogPage(Model model, RedirectAttributes attributes,
    // @PathVariable String uid) {
    // model.addAttribute("logs", wls.getAllWorkLogByUid(uid)); // 作業ログをModelに登録

    // model.addAttribute("comments", cs.getAllCommentByUid(uid)); // コメントをModelに登録

    // model.addAttribute(new LidForm()); // 作業ログIDフォームをModelに登録

    // model.addAttribute(new WorkLogForm()); // 作業ログフォームをModelに登録

    // model.addAttribute(new CommentForm()); // コメントフォームをModelに登録

    // model.addAttribute("works", ws.getAllWork()); // 作業リストをModelに登録

    // String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    // model.addAttribute("today", today); // 今日の日付をModelに登録

    // model.addAttribute(new QueryForm()); // 期間指定フォームをModelに登録

    // model.addAttribute("times",
    // wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, today, today)));
    // // 作業時間をModelに登録

    // return "lookBack"; // 作業ログ編集ページ
    // }

    /**
     * 作業ログ編集ページを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/{wid}")
    public String showLogPage(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        String alldate = "2000-01-05"; // 全期間指定のパス
        String week = "1974-12-05"; // 1週間指定のパス

        String allwork = "all"; // 全作業指定のパス
        String unspecified = "unspecified"; // 作業未指定のパス

        Date today = new Date();
        String todayst = new SimpleDateFormat("yyyy-MM-dd").format(today);

        List<WorkLogWithWnameDto> logs;

        if (wid.equals(allwork)) { // 全ての作業に対して
            if (since.equals(alldate)) { // sinceの値が全期間を表すものだったら
                logs = wls.getAllWorkLogByUid(uid);

                model.addAttribute("logs", logs); // 全ての作業ログをModelに登録

                model.addAttribute("comments", cs.getAllCommentByUid(uid)); // 全てのコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録

                model.addAttribute("allflag", true); // 全期間フラグオン
            } else if (since.equals(week)) {
                Calendar cal = Calendar.getInstance();

                // 7日減算
                cal.setTime(today);
                cal.add(Calendar.DATE, -7);
                since = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                until = todayst;

                logs = wls.getWorkLogSettingPeriod(uid, since, until);

                model.addAttribute("logs", logs); // 1週間の作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentSettingPeriod(uid, since, until)); // 1週間のコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 1週間の作業時間をModelに登録
            } else {
                logs = wls.getWorkLogSettingPeriod(uid, since, until);

                model.addAttribute("logs", logs); // 作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentSettingPeriod(uid, since, until)); // コメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録
            }

            model.addAttribute("wname", "全作業"); // 作業名をModelに登録
        } else if (wid.equals(unspecified)) { // 未指定に対して
            if (since.equals(alldate)) { // sinceの値が全期間を表すものだったら
                logs = wls.getWorkLogByWid(uid, wid);

                model.addAttribute("logs", logs); // 全ての作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWid(uid, "0")); // 全てのコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録

                model.addAttribute("allflag", true); // 全期間フラグオン
            } else if (since.equals(week)) {
                Calendar cal = Calendar.getInstance();

                // 7日減算
                cal.setTime(today);
                cal.add(Calendar.DATE, -7);
                since = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                until = todayst;

                logs = wls.getWorkLogByWidSettingPeriod(uid, wid, since, until);

                model.addAttribute("logs", logs); // 1週間の作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWidSettingPeriod(uid, "0", since, until)); // 1週間のコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 1週間の作業時間をModelに登録
            } else {
                logs = wls.getWorkLogByWidSettingPeriod(uid, wid, since, until);

                model.addAttribute("logs", logs); // 作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWidSettingPeriod(uid, "0", since, until)); // コメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録
            }

            model.addAttribute("wname", "未指定"); // 作業名をModelに登録
        } else { // 指定された作業に対して
            if (since.equals(alldate)) { // sinceの値が全期間を表すものだったら
                logs = wls.getWorkLogByWid(uid, wid);

                model.addAttribute("logs", logs); // 全ての作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWid(uid, wid)); // 全てのコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録

                model.addAttribute("allflag", true); // 全期間フラグオン
            } else if (since.equals(week)) {
                Calendar cal = Calendar.getInstance();

                // 7日減算
                cal.setTime(today);
                cal.add(Calendar.DATE, -6);
                since = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                until = todayst;

                logs = wls.getWorkLogByWidSettingPeriod(uid, wid, since, until);

                model.addAttribute("logs", logs); // 1週間の作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWidSettingPeriod(uid, wid, since, until)); // 1週間のコメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 1週間の作業時間をModelに登録
            } else {
                logs = wls.getWorkLogByWidSettingPeriod(uid, wid, since, until);

                model.addAttribute("logs", logs); // 作業ログをModelに登録

                model.addAttribute("comments", cs.getCommentByWidSettingPeriod(uid, wid, since, until)); // コメントをModelに登録

                model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // 作業時間をModelに登録
            }

            model.addAttribute("wname", ws.getWork(wid).getWname()); // 作業名をModelに登録
        }

        model.addAttribute(new LidForm()); // 作業ログIDフォームをModelに登録

        model.addAttribute(new WorkLogForm()); // 作業ログフォームをModelに登録

        model.addAttribute(new CidForm()); // コメントIDをModelに登録

        model.addAttribute("works", ws.getAllWork()); // 作業リストをModelに登録

        model.addAttribute("today", todayst); // 今日の日付をModelに登録

        model.addAttribute(new QueryForm()); // 期間指定フォームをModelに登録

        // 振り返り期間情報をModelに登録
        model.addAttribute("since", since);
        model.addAttribute("until", until);

        model.addAttribute("all", alldate); // 全期間指定のパスをModelに登録

        model.addAttribute("week", week); // 全期間指定のパスをModelに登録

        model.addAttribute("wid", wid); // 作業IDをModelに登録

        model.addAttribute("widForm", new WidForm()); // 作業IDフォームをModelに登録

        model.addAttribute("meetingLogForm", new MeetingLogForm()); // 作業IDフォームをModelに登録

        model.addAttribute("achivementForm", new AchivementForm()); // 成果フォームをModelに登録

        model.addAttribute("commentForm", new CommentForm()); // コメントフォームをModelに登録

        return "lookBack"; // 作業ログ編集ページ
    }

    /**
     * 期間指定フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/mode/datequery")
    public String addLogQueryForm(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        attributes.addFlashAttribute("addDateQueryMode", true); // 期間指定フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
    }

    /**
     * 期間指定した作業ログ編集ページを渡す
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/query/{wid}")
    public String LogQueryByDate(Model model, RedirectAttributes attributes, @ModelAttribute @Validated QueryForm form,
            BindingResult bindingResult, @PathVariable String uid, @PathVariable String wid) {
        return "redirect:/" + uid + "/logs/lookback/" + form.getSince() + "~" + form.getUntil() + "/" + wid; // viewを転換;
    }

    /**
     * 作業指定フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/mode/widquery")
    public String addLogQueryWidForm(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        attributes.addFlashAttribute("addWidQueryMode", true); // 期間指定フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
    }

    /**
     * 作業指定した作業ログ編集ページを渡す
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/query")
    public String LogQueryByWid(Model model, RedirectAttributes attributes, @ModelAttribute @Validated WidForm form,
            BindingResult bindingResult, @PathVariable String uid, @PathVariable String since,
            @PathVariable String until) {
        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + form.getWid(); // viewを転換;
    }

    // /**
    // * 全ての作業ログとコメントを表示
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackall")
    // public String showAllLog(Model model, RedirectAttributes attributes,
    // @PathVariable String uid) {
    // model.addAttribute("logs", wls.getAllWorkLogByUid(uid)); // 作業ログをModelに登録

    // model.addAttribute("comments", cs.getAllCommentByUid(uid)); // コメントをModelに登録

    // model.addAttribute(new LidForm()); // 作業ログIDフォームをModelに登録

    // model.addAttribute(new WorkLogForm()); // 作業ログフォームをModelに登録

    // model.addAttribute(new CommentForm()); // コメントフォームをModelに登録

    // model.addAttribute("works", ws.getAllWork()); // 作業リストをModelに登録

    // model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(new
    // Date())); // 今日の日付をModelに登録

    // model.addAttribute(new QueryForm()); // 期間指定フォームをModelに登録

    // model.addAttribute("times",
    // wls.caluculateAllWorkTime(wls.getAllWorkLogByUid(uid))); // 作業時間をModelに登録

    // return "lookBackAll"; // 全ての作業ログ編集ページ
    // }

    // /**
    // * 期間指定フォームを全ての作業ログ編集ページを渡す
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackall/mode/query")
    // public String addWorkLogQueryFormToAllLog(Model model, RedirectAttributes
    // attributes, @PathVariable String uid) {
    // attributes.addFlashAttribute("addQueryMode", true); // 期間指定フラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // /**
    // * 期間指定した作業ログとコメントを表示
    // *
    // * @param model
    // * @param attributes
    // * @param form
    // * @param bindingResult
    // * @param uid
    // * @return 期間指定した作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackquery")
    // public String showQueryLog(Model model, RedirectAttributes attributes,
    // @ModelAttribute @Validated QueryForm form,
    // BindingResult bindingResult, @PathVariable String uid) {
    // String sincest = form.getSince();
    // String untilst = form.getUntil();

    // model.addAttribute("logs", wls.getWorkLogSettingPeriod(uid, sincest,
    // untilst)); // 作業ログをModelに登録

    // model.addAttribute("comments", cs.getCommentSettingPeriod(uid, sincest,
    // untilst)); // コメントをModelに登録

    // // model.addAttribute(new LidForm()); // 作業ログIDフォームをModelに登録

    // // model.addAttribute(new WorkLogForm()); // 作業ログフォームをModelに登録

    // // model.addAttribute(new CommentForm()); // コメントフォームをModelに登録

    // model.addAttribute("works", ws.getAllWork()); // 作業リストをModelに登録

    // model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(new
    // Date())); // 今日の日付をModelに登録

    // model.addAttribute("since", form.getSince()); // sinceをModelに登録

    // model.addAttribute("until", form.getUntil()); // untilをModelに登録

    // model.addAttribute(new QueryForm()); // 期間指定フォームをModelに登録

    // model.addAttribute("times",
    // wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, sincest,
    // untilst))); // 作業時間をModelに登録

    // return "lookbackquery"; // 期間指定した作業ログ編集ページ
    // }

    // /**
    // * 期間指定フォームを期間指定した作業ログ編集ページを渡す
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return 期間指定した作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackquery/mode/query")
    // public String addWorkLogQueryFormToQueryLog(Model model, RedirectAttributes
    // attributes,
    // @ModelAttribute @Validated QueryForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // attributes.addFlashAttribute("addQueryMode", true); // 期間指定フラグオン

    // attributes.addFlashAttribute("since", form.getSince()); // sinceをModelに登録

    // attributes.addFlashAttribute("until", form.getUntil()); // untilをModelに登録

    // return "redirect:/" + uid + "/logs/lookbackquery"; // viewを転換
    // }

    /**
     * 前期間の振り返りに移動
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{sincest}~{untilst}/{wid}/daybefore")
    public String showADayBeforeLogPage(@PathVariable String uid,
            @PathVariable String sincest, @PathVariable String untilst, @PathVariable String wid) {
        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
        }

        int date = ((int) (until.getTime() - since.getTime()) / (1000 * 60 * 60 * 24)) + 1;

        Calendar cal = Calendar.getInstance();

        // 期間の日数分減算
        cal.setTime(since);
        cal.add(Calendar.DATE, -date);
        since = cal.getTime();
        cal.setTime(until);
        cal.add(Calendar.DATE, -date);
        until = cal.getTime();

        // Date型をString型に変換する
        sincest = new SimpleDateFormat("yyyy-MM-dd").format(since);
        untilst = new SimpleDateFormat("yyyy-MM-dd").format(until);

        return "redirect:/" + uid + "/logs/lookback/" + sincest + "~" + untilst + "/" + wid; // 作業ログ編集ページ
    }

    /**
     * 後期間の振り返りに移動
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{sincest}~{untilst}/{wid}/dayafter")
    public String showADayAfterLogPage(@PathVariable String uid,
            @PathVariable String sincest, @PathVariable String untilst, @PathVariable String wid) {
        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
        }

        int date = ((int) (until.getTime() - since.getTime()) / (1000 * 60 * 60 * 24)) + 1;

        Calendar cal = Calendar.getInstance();

        // 期間の日数分減算
        cal.setTime(since);
        cal.add(Calendar.DATE, date);
        since = cal.getTime();
        cal.setTime(until);
        cal.add(Calendar.DATE, date);
        until = cal.getTime();

        // Date型をString型に変換する
        sincest = new SimpleDateFormat("yyyy-MM-dd").format(since);
        untilst = new SimpleDateFormat("yyyy-MM-dd").format(until);

        return "redirect:/" + uid + "/logs/lookback/" + sincest + "~" + untilst + "/" + wid; // 作業ログ編集ページ
    }

    /* -------------------- ミーティングログ作成 -------------------- */

    /**
     * ミーティングログ作成
     *
     * @param model
     * @param attributes
     * @param MeetingLogForm
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @return ミーティングログ生成ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/meetinglog")
    public String showMeetingLog(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated MeetingLogForm form, BindingResult bindingResult, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until) {
        model.addAttribute("times", wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, since, until))); // 作業時間のリストををModelに登録

        model.addAttribute("meetinglogs", cs.getMeetingLog(cs.getAchivement(uid, since, until))); // ミーティングログををModelに登録

        model.addAttribute("since", since); // sinceをModelに登録

        model.addAttribute("until", until); // untilをModelに登録

        model.addAttribute("all", "allwork"); // 全作業指定のパスをModelに登録

        model.addAttribute("others", cs.getOthers(uid, since, until)); // OtherのリストをModelに登録

        model.addAttribute("todoid", us.getUser(uid).getTodoid()); // ToDoリストのユーザIDをModelに登録

        return "meetingLog"; // ミーティングログ生成ページ
    }

    /* -------------------- カレンダー表示 -------------------- */

    /**
     * カレンダー表示
     *
     * @param model
     * @param attributes
     * @param uid
     * @return カレンダー表示
     */
    @GetMapping("/{uid}/calendar")
    public String showCalendar(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        model.addAttribute("logs", wls.getAllWorkLogByUid(uid)); // ユーザに紐づく全ての作業ログをModelに登録

        LinkedHashMap<String, Integer> times = new LinkedHashMap<String, Integer>();

        ArrayList<String> dates = new ArrayList<String>();

        String date;
        String datecomp = "";

        // 日付のリストを作成
        for (WorkLogWithWnameDto log : wls.getAllWorkLogByUid(uid)) {
            date = log.getUntildate();

            if (!date.equals(datecomp)) { // 日時が被っていたら
                dates.add(date);

                datecomp = date;
            }
        }

        for (String datewl : dates) {
            List timedtos = wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, datewl, datewl));

            WorkTimeWithWnameDto timedto = wls.caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, datewl, datewl))
                    .get(timedtos.size() - 1);

            times.put(datewl, timedto.getSecond());
        }

        // System.out.println(times);

        model.addAttribute("times", times); // 作業時間をModelに登録

        return "calendar";
    }

    /* -------------------- ランキング表示 -------------------- */

    /**
     * ランキング表示
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @return ランキングページ
     */
    @GetMapping("/{uid}/ranking/{since}~{until}")
    public String showRanking(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until) {
        String alldate = "2000-01-05"; // 全期間指定のパス
        String week = "1974-12-05"; // 1週間指定のパス

        Date today = new Date();
        String todayst = new SimpleDateFormat("yyyy-MM-dd").format(today);

        if (since.equals(alldate)) { // sinceの値が全期間を表すものだったら
            model.addAttribute("ranking", wls.getAllWorkTimeRanking()); // 全期間の総合作業時間ランキングをModelに登録

            model.addAttribute("allflag", true); // 全期間フラグオン

            List<WorkTimeWithWnameDto> times = wls.caluculateAllWorkTime(wls.getAllWorkLogByUid(uid));

            model.addAttribute("mytime", times.get(times.size() - 1).getSecond()); // 自分の作業時間をModelに登録
        } else if (since.equals(week)) {
            Calendar cal = Calendar.getInstance();

            // 7日減算
            cal.setTime(today);
            cal.add(Calendar.DATE, -7);
            since = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

            until = todayst;

            model.addAttribute("ranking", wls.getWorkTimeRankingSettingPeriod(since, until)); // 直近1週間の総合作業時間ランキングをModelに登録

            List<WorkTimeWithWnameDto> times = wls
                    .caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, since, until));

            model.addAttribute("mytime", times.get(times.size() - 1).getSecond()); // 自分の作業時間をModelに登録
        } else {
            model.addAttribute("ranking", wls.getWorkTimeRankingSettingPeriod(since, until)); // 期間指定された総合作業時間ランキングをModelに登録

            List<WorkTimeWithWnameDto> times = wls
                    .caluculateAllWorkTime(wls.getWorkLogSettingPeriod(uid, since, until));

            model.addAttribute("mytime", times.get(times.size() - 1).getSecond()); // 自分の作業時間をModelに登録
        }

        model.addAttribute("today", todayst); // 今日の日付をModelに登録

        model.addAttribute(new QueryForm()); // 期間指定フォームをModelに登録

        // 期間指定情報をModelに登録
        model.addAttribute("since", since);
        model.addAttribute("until", until);

        model.addAttribute("all", alldate); // 全期間指定のパスをModelに登録

        model.addAttribute("week", week); // 全期間指定のパスをModelに登録

        String allwork = "all"; // 全作業指定のパス

        model.addAttribute("allwork", allwork); // 全作業指定のパスをModelに登録

        model.addAttribute("nickname", us.getUser(uid).getNickname()); // ニックネームをModelに登録

        return "ranking"; // ランキングページ
    }

    /**
     * 期間指定フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @return ランキングページ
     */
    @GetMapping("/{uid}/ranking/{since}~{until}/mode/datequery")
    public String addRankingQueryForm(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until) {
        attributes.addFlashAttribute("addDateQueryMode", true); // 期間指定フラグオン

        return "redirect:/" + uid + "/ranking/" + since + "~" + until; // ランキングページ
    }

    /**
     * 期間指定したランキング表示
     *
     * @param model
     * @param attributes
     * @param QueryForm
     * @param bindingResult
     * @param uid
     * @return ランキングページ
     */
    @GetMapping("/{uid}/ranking/query")
    public String rankingQueryByDate(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated QueryForm form,
            BindingResult bindingResult, @PathVariable String uid) {
        return "redirect:/" + uid + "/ranking/" + form.getSince() + "~" + form.getUntil(); // ランキングページ
    }

    /**
     * 前期間のランキングに移動
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return ランキングページ
     */
    @GetMapping("/{uid}/ranking/{sincest}~{untilst}/daybefore")
    public String showADayBeforeRankingPage(@PathVariable String uid,
            @PathVariable String sincest, @PathVariable String untilst) {
        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
        }

        int date = ((int) (until.getTime() - since.getTime()) / (1000 * 60 * 60 * 24)) + 1;

        Calendar cal = Calendar.getInstance();

        // 期間の日数分減算
        cal.setTime(since);
        cal.add(Calendar.DATE, -date);
        since = cal.getTime();
        cal.setTime(until);
        cal.add(Calendar.DATE, -date);
        until = cal.getTime();

        // Date型をString型に変換する
        sincest = new SimpleDateFormat("yyyy-MM-dd").format(since);
        untilst = new SimpleDateFormat("yyyy-MM-dd").format(until);

        return "redirect:/" + uid + "/ranking/" + sincest + "~" + untilst; // ランキングページ
    }

    /**
     * 後期間のランキングに移動
     *
     * @param uid
     * @param sincest
     * @param untilst
     * @return ランキングページ
     */
    @GetMapping("/{uid}/ranking/{sincest}~{untilst}/dayafter")
    public String showADayAfterRankingPage(@PathVariable String uid,
            @PathVariable String sincest, @PathVariable String untilst) {
        Date since = new Date();
        Date until = new Date();

        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            // String型をDate型に変換する
            since = sdFormat.parse(sincest);
            until = sdFormat.parse(untilst);
        } catch (ParseException e) {
        }

        int date = ((int) (until.getTime() - since.getTime()) / (1000 * 60 * 60 * 24)) + 1;

        Calendar cal = Calendar.getInstance();

        // 期間の日数分減算
        cal.setTime(since);
        cal.add(Calendar.DATE, date);
        since = cal.getTime();
        cal.setTime(until);
        cal.add(Calendar.DATE, date);
        until = cal.getTime();

        // Date型をString型に変換する
        sincest = new SimpleDateFormat("yyyy-MM-dd").format(since);
        untilst = new SimpleDateFormat("yyyy-MM-dd").format(until);

        return "redirect:/" + uid + "/ranking/" + sincest + "~" + untilst; // ランキングページ
    }

    /* -------------------- ログを手動で追加 -------------------- */

    /**
     * ログ手動追加フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/mode/add")
    public String addLogCreateForm(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        attributes.addFlashAttribute("addWorkLogMode", true); // 作業ログ追加フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
    }

    /**
     * 作業ログを手動で追加する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @PostMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/add")
    public String addWorkLogManually(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated WorkLogForm form, BindingResult bindingResult, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        Date today = new Date();
        LoginLog log = lls.getLoginLog(uid);

        int maximumloginstreak = log.getMaximumloginstreak();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Date型をString型に変換する
        String todayst = sdFormat.format(today);

        String lastloginst = new String();

        if (log.getLastlogin() != null) {
            lastloginst = sdFormat.format(log.getLastlogin());
        }

        try {
            wls.createWorkLogManually(uid, form.getWid(), form.getDatesince(), form.getTimesince(), form.getDateuntil(),
                    form.getTimeuntil()); // 作業ログ追加
        } catch (WorkLogValidationException e) {
            attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isWorkLogCoudNotCreateError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        } catch (OtherValidationException e) {
            attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        attributes.addFlashAttribute("workLogCreated", true); // 作業ログ作成完了フラグオン

        if (!todayst.equals(lastloginst)) { // 本日初回ログインならば
            attributes.addFlashAttribute("firstLoginToday", true); // 本日の初回ログインフラグオン
            attributes.addFlashAttribute("loginlog", log); // モデルにログインログを登録
        }

        log = lls.getLoginLog(uid);

        if (log.getLoginstreak() > maximumloginstreak) {
            attributes.addFlashAttribute("renewRecord", true); // 最大連続ログイン記録更新フラグオン
        }

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
    }

    /**
     * コメントを追加
     *
     * @param model
     * @param attributes
     * @param CommentForm
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @PostMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/comment/add")
    public String addComment(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated CommentForm form, BindingResult bindingResult, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("addCommentMode", true); // コメント入力フラグオン

            attributes.addFlashAttribute("isCommentFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs"; // viewを転換
        }

        try {
            cs.createComment(form.toEntity()); // コメント追加
        } catch (Exception e) {
            attributes.addFlashAttribute("addCommentMode", true); // コメント入力フラグオン

            attributes.addFlashAttribute("isWorkDoseNotExist", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
        }

        attributes.addFlashAttribute("commentCreated", true); // コメント作成完了フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
    }

    // /**
    // * 作業ログ手動追加フォームを全ての作業ログ編集ページに渡す
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackall/mode/add")
    // public String addWorkLogCreateFormFromAllWorkLog(Model model,
    // RedirectAttributes attributes,
    // @PathVariable String uid) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業ログ追加フラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // /**
    // * 作業ログを全ての作業ログ編集ページから手動で追加する
    // *
    // * @param model
    // * @param attributes
    // * @param WorkLogForm
    // * @param bindingResult
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @PostMapping("/{uid}/logs/lookbackall/add")
    // public String addWorkLogManuallyFromAllWorkLog(Model model,
    // RedirectAttributes attributes,
    // @ModelAttribute @Validated WorkLogForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // if (bindingResult.hasErrors()) { // formのバリゼーション違反
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // try {
    // wls.createWorkLogManually(uid, form.getWid(), form.getDatesince(),
    // form.getTimesince(), form.getDateuntil(),
    // form.getTimeuntil()); // 作業ログ追加
    // } catch (WorkLogValidationException e) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogCoudNotCreateError", true); //
    // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // } catch (OtherValidationException e) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // return "redirect:/" + uid + "/logs/lookbackall"; // 全ての作業ログ編集ページ
    // }

    // /**
    // * 作業ログ手動追加フォームを期間指定した作業ログ編集ページに渡す
    // *
    // * @param model
    // * @param attributes
    // * @param uid
    // * @return 期間指定した作業ログ編集ページ
    // */
    // @GetMapping("/{uid}/logs/lookbackquery/mode/add")
    // public String addWorkLogCreateFormFromQueryWorkLog(Model model,
    // RedirectAttributes attributes,
    // @ModelAttribute @Validated QueryForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業ログ追加フラグオン

    // return "redirect:/" + uid + "/logs/lookbackquery"; // viewを転換
    // }

    // /**
    // * 作業ログを期間指定した作業ログ編集ページから手動で追加する
    // *
    // * @param model
    // * @param attributes
    // * @param WorkLogForm
    // * @param bindingResult
    // * @param uid
    // * @return 期間指定した作業ログ編集ページ
    // */
    // @PostMapping("/{uid}/logs/lookbackquery/add")
    // public String addWorkLogManuallyFromQueryWorkLog(Model model,
    // RedirectAttributes attributes,
    // @ModelAttribute @Validated WorkLogForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // if (bindingResult.hasErrors()) { // formのバリゼーション違反
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackquery"; // viewを転換
    // }

    // try {
    // wls.createWorkLogManually(uid, form.getWid(), form.getDatesince(),
    // form.getTimesince(), form.getDateuntil(),
    // form.getTimeuntil()); // 作業ログ追加
    // } catch (WorkLogValidationException e) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogCoudNotCreateError", true); //
    // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackquery"; // viewを転換
    // } catch (OtherValidationException e) {
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackquery"; // viewを転換
    // }

    // return "redirect:/" + uid + "/logs/lookbackquery"; // 全ての作業ログ編集ページ
    // }

    /* -------------------- 削除 -------------------- */

    /**
     * 作業ログを削除
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @PostMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/delete")
    public String deleteWorkLog(Model model, RedirectAttributes attributes, @ModelAttribute @Validated LidForm form,
            BindingResult bindingResult, @PathVariable String uid, @PathVariable String since,
            @PathVariable String until, @PathVariable String wid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isLidFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        try {
            wls.deleteWorkLog(uid, form.getLid()); // 作業ログを削除
        } catch (Exception e) {
            attributes.addFlashAttribute("isWorkLogNotBeDeletedError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        attributes.addFlashAttribute("workLogDeleted", true); // 作業ログ削除完了フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
    }

    // /**
    // * 作業ログを全ての作業ログ編集ページから削除
    // *
    // * @param model
    // * @param attributes
    // * @param LidForm
    // * @param bindingResult
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @PostMapping("/{uid}/logs/lookbackall/delete")
    // public String deleteWorkLogFromAllWorkLog(Model model, RedirectAttributes
    // attributes,
    // @ModelAttribute @Validated LidForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // if (bindingResult.hasErrors()) { // formのバリゼーション違反
    // attributes.addFlashAttribute("addWorkLogMode", true); // 作業登録フラグオン

    // attributes.addFlashAttribute("isWorkLogFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // try {
    // wls.deleteWorkLog(uid, form.getLid()); // 作業ログを削除
    // } catch (Exception e) {
    // attributes.addFlashAttribute("isWorkLogNotBeDeletedError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // return "redirect:/" + uid + "/logs/lookbackall"; // 全ての作業ログ編集ページ
    // }
}
