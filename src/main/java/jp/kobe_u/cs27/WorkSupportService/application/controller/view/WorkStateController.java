package jp.kobe_u.cs27.WorkSupportService.application.controller.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeWithWnameDto;
import jp.kobe_u.cs27.WorkSupportService.domain.service.CommentService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.LoginLogService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.UserService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkLogService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkStateService;

/**
 * 作業状況のコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class WorkStateController {
    /** 作業状況のサービス */
    private final WorkStateService wss;
    /** ユーザのサービス */
    private final UserService us;
    /** 作業ログのサービス */
    private final WorkLogService wls;
    /** コメントのサービス */
    private final CommentService cs;
    /** ログインログのサービス */
    private final LoginLogService lls;

    /**
     * 作業状況のページを渡す
     *
     * @param model
     * @param uid
     * @return 作業状況ページ
     */
    @GetMapping("/{uid}/workstate")
    public String showWorkStatePage(Model model, @PathVariable String uid) {
        // if (bindingResult.hasErrors()) { // ユーザIDに使用できない文字が含まれていた場合
        // attributes.addFlashAttribute("isUidValidationError", true); // エラーフラグをオンにする

        // return "redirect:/"; // 初期画面に戻る
        // }

        // model.addAttribute("uid", form.getUid()); // ユーザIDをModelに追加する

        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        model.addAttribute("states", wss.getAllWorkStateWithName()); // 作業状況リストをModelに追加

        model.addAttribute("nickname", us.getUser(uid).getNickname()); // ユーザのnicknameをModelに追加

        return "workState"; // 作業状況ページ
    }

    /**
     * ユーザページを渡す
     *
     * @param model
     * @param myuid
     * @param uid
     * @return ユーザページ
     */
    @GetMapping("/{myuid}/userpage/{uid}")
    public String showUserPage(Model model, @PathVariable String myuid, @PathVariable String uid) {
        model.addAttribute("myuid", myuid); // ユーザのuidをModelに追加

        model.addAttribute("uid", uid); // 開いているページのユーザのuidをModelに追加

        model.addAttribute("nickname", us.getUser(uid).getNickname()); // ユーザのnicknameをModelに追加

        int length;

        List logs = wls.getAllWorkLogByUid(uid);

        model.addAttribute("logsforcalendar", logs); // ユーザの作業ログをModelに追加

        model.addAttribute("times", wls.caluculateAllWorkTime(logs)); // ユーザの作業時間をModelに追加

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

        model.addAttribute("datetimes", times); // 作業時間をModelに登録

        length = logs.size();

        if (length > 5) { // リストのサイズが5より大きければ
            logs = logs.subList(0, 5);
        } else {
            logs = logs.subList(0, length);
        }

        List comments = cs.getAllCommentByUid(uid);

        length = comments.size();

        if (length > 5) { // リストのサイズが5より大きければ
            comments = comments.subList(0, 5);
        } else {
            comments = comments.subList(0, length);
        }

        model.addAttribute("logs", logs); // ユーザの直近5つの作業ログをModelに追加

        model.addAttribute("comments", comments); // ユーザの直近5つのコメントをModelに追加

        Date today = new Date();
        String todayst = new SimpleDateFormat("yyyy-MM-dd").format(today);

        Calendar cal = Calendar.getInstance();

        // 7日減算
        cal.setTime(today);
        cal.add(Calendar.DATE, -7);
        String since = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        String until = todayst;

        logs = wls.getWorkLogSettingPeriod(uid, since, until);

        model.addAttribute("weektimes", wls.caluculateAllWorkTime(logs)); // 1週間の作業時間をModelに登録

        model.addAttribute("loginlog", lls.getLoginLog(uid));// ユーザのログインログをModelに追加

        return "userPage";
    }
}