package jp.kobe_u.cs27.WorkSupportService.application.controller.view;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import jp.kobe_u.cs27.WorkSupportService.application.form.CommentForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkTimeChallengeForm;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkEventValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.UserValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkTimeChallengeDto;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.LoginLog;
import jp.kobe_u.cs27.WorkSupportService.domain.service.LoginLogService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.UserService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkEventService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkStateService;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkTimeChallengeService;

/**
 * 作業イベントのコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class WorkEventController {
    /** 作業イベントのサービス */
    private final WorkEventService wes;
    /** ユーザのサービス */
    private final UserService us;
    /** 作業のサービス */
    private final WorkService ws;
    /** 作業状況のサービス */
    private final WorkStateService wss;
    /** ログインログのサービス */
    private final LoginLogService lls;
    /** 目標作業時間のサービス */
    private final WorkTimeChallengeService wtcs;

    /**
     * ログイン後のホーム
     *
     * @param model
     * @param attributes
     * @param uid
     * @return logs
     */
    @GetMapping("/{uid}/logs")
    public String showHomePage(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        String nickname;

        // ユーザIDから名前を取得する
        // ユーザが登録済みかどうかの確認も兼ねている
        try {
            nickname = us.getUser(uid).getNickname();
        } catch (UserValidationException e) { // ユーザIDが未登録であった場合
            attributes.addFlashAttribute("isUserDoesNotExistError", true); // エラーフラグをオンにする

            // System.out.println(e.getMessage());

            return "redirect:/"; // 初期ページに戻る
        }

        // ユーザIDとニックネームをModelに追加する
        model.addAttribute("uid", uid);
        model.addAttribute("nickname", nickname);

        model.addAttribute("works", ws.getAllWork()); // モデルに作業リストを登録

        if (!wss.getWorkState(uid).getWid().equals("0")) { // 作業中なら
            model.addAttribute("wname", wss.getWorkStateWithWname(uid).getWname()); // モデルに作業中の作業名を登録
            model.addAttribute("isWorking", true); // 作業中フラグをオンにする
        } else {
            model.addAttribute("isWorking", false); // 作業中フラグをオフにする
        }

        model.addAttribute(new WidForm()); // モデルに作業IDフォームを登録

        model.addAttribute(new CommentForm()); // モデルにコメントフォームを登録

        model.addAttribute("number", wss.getNumberOfWorking(uid)); // モデルに現在作業中のユーザの人数を登録

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        model.addAttribute("today", today); // 今日の日付をModelに登録

        model.addAttribute("allwork", "all"); // 全作業を指定する文字列をModelに登録

        model.addAttribute(new WorkTimeChallengeForm()); // モデルに目標作業時間フォームを登録

        WorkTimeChallengeDto dto = wtcs.getWorkTimeChallenge(uid);

        if (dto != null) { // 目標作業時間が存在するなら
            model.addAttribute("isChallengeExist", true); // 目標作業時間存在フラグをオン

            model.addAttribute("challenge", dto); // モデルに目標作業時間を登録
        }

        return "logs";
    }

    /* -------------------- 作業記録 -------------------- */

    /**
     * 作業をstart
     *
     * @param model
     * @param attributes
     * @param WidForm
     * @param bindingResul
     * @param uid
     * @return redirect:/logs
     */
    @PostMapping("/{uid}/logs/startwork")
    public String pushStartWorkEvent(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated WidForm form, BindingResult bindingResul, @PathVariable String uid) {
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
            wes.startWorkEvent(uid, form.getWid());
        } catch (WorkEventValidationException e) {
            attributes.addFlashAttribute("isWorkDidNotEndError", true); // エラーフラグをオンにする
        }

        if (!todayst.equals(lastloginst)) { // 本日初回ログインならば
            attributes.addFlashAttribute("firstLoginToday", true); // 本日の初回ログインフラグオン
            attributes.addFlashAttribute("loginlog", log); // モデルにログインログを登録
        }

        log = lls.getLoginLog(uid);

        if (log.getLoginstreak() > maximumloginstreak) {
            attributes.addFlashAttribute("renewRecord", true); // 最大連続ログイン記録更新フラグオン
        }

        return "redirect:/" + uid + "/logs";
    }

    /**
     * 作業をend
     *
     * @param model
     * @param attributes
     * @param Widform
     * @param bindingResul
     * @param uid
     * @return redirect:/logs
     */
    @PostMapping("/{uid}/logs/endwork")
    public String pushEndWorkEvent(Model model, RedirectAttributes attributes, @ModelAttribute @Validated WidForm form,
            BindingResult bindingResul, @PathVariable String uid) {
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
            wes.endWorkEvent(uid, form.getWid());
        } catch (WorkEventValidationException e) {
            attributes.addFlashAttribute("isWorkCanNotEndError", true); // エラーフラグをオンにする
        }

        if (!todayst.equals(lastloginst)) { // 本日初回ログインならば
            attributes.addFlashAttribute("firstLoginToday", true); // 本日の初回ログインフラグオン
            attributes.addFlashAttribute("loginlog", log); // モデルにログインログを登録
        }

        log = lls.getLoginLog(uid);

        if (log.getLoginstreak() > maximumloginstreak) {
            attributes.addFlashAttribute("renewRecord", true); // 最大連続ログイン記録更新フラグオン
        }

        return "redirect:/" + uid + "/logs";
    }
}
