package jp.kobe_u.cs27.WorkSupportService.application.controller.view;

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
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkTimeChallengeForm;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.OtherValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkTimeChallengeValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkTimeChallengeService;

/**
 * 目標作業時間のコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class WorkTimeChallengeController {
    /** 目標作業時間のサービス */
    private final WorkTimeChallengeService wtcs;

    /**
     * 目標作業時間フォームをログイン後のホームに渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @return ログイン後のホーム
     */
    @GetMapping("/{uid}/challenge/mode/add")
    public String addWorkTimeChallengeForm(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        attributes.addFlashAttribute("addWorkTimeChallengeMode", true); // コメント入力フラグオン

        return "redirect:/" + uid + "/logs"; // viewを転換
    }

    /**
     * 目標作業時間を設定
     *
     * @param model
     * @param attributes
     * @param WorkTimeChallengeForm
     * @param bindingResult
     * @param uid
     * @return ログイン後のホーム
     */
    @PostMapping("/{uid}/challenge/add")
    public String addWorkTimeChallenge(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated WorkTimeChallengeForm form, BindingResult bindingResult,
            @PathVariable String uid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("addWorkTimeChallengeMode", true); // 目標作業時間入力フラグオン

            attributes.addFlashAttribute("isWorkTimeChallengeFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs"; // viewを転換
        }

        try {
            wtcs.createWorkTimeChallenge(uid, form.getUntilst(), form.getHour() * 3600); // 目標作業時間を設定
        } catch (WorkTimeChallengeValidationException e) {
            attributes.addFlashAttribute("addWorkTimeChallengeMode", true); // 目標作業時間入力フラグオン

            attributes.addFlashAttribute("isWorkTimeChallengeCoudNotCreateError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs"; // viewを転換
        } catch (OtherValidationException e) {
            attributes.addFlashAttribute("addWorkTimeChallengeMode", true); // 目標作業時間入力フラグオン

            attributes.addFlashAttribute("isWorkTimeChallengeFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs"; // viewを転換
        }

        attributes.addFlashAttribute("WorkTimeChallengeCreated", true); // 目標作業時間設定完了フラグオン

        return "redirect:/" + uid + "/logs"; // ログイン後のホーム
    }

    /* -------------------- 目標作業時間削除 -------------------- */

    /**
     * 直近の目標作業時間を削除
     *
     * @param model
     * @param attributes
     * @param uid
     * @return
     */
    @PostMapping("/{uid}/challenge/delete")
    public String deleteWorkTimeChallenge(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        wtcs.deleteWorkTimeChallenge(uid, wtcs.getWorkTimeChallenge(uid).getWtcid());

        attributes.addFlashAttribute("WorkTimeChallengeDeleted", true); // 目標作業時間削除完了フラグオン

        return "redirect:/" + uid + "/logs"; // ログイン後のホーム
    }
}