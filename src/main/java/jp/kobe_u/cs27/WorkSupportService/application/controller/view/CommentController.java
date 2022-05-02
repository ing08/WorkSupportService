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
import jp.kobe_u.cs27.WorkSupportService.application.form.AchivementForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.CommentForm;
import jp.kobe_u.cs27.WorkSupportService.domain.service.CommentService;

/**
 * コメントのコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class CommentController {
    /** コメントのサービス */
    private final CommentService cs;

    /**
     * コメントフォームをログイン後のホームに渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @return ログイン後のホーム
     */
    @GetMapping("/{uid}/comment/mode/add")
    public String addCommentForm(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        attributes.addFlashAttribute("addCommentMode", true); // コメント入力フラグオン

        return "redirect:/" + uid + "/logs"; // viewを転換
    }

    /**
     * コメントを追加
     *
     * @param model
     * @param attributes
     * @param CommentForm
     * @param bindingResult
     * @param uid
     * @return ログイン後のホーム
     */
    @PostMapping("/{uid}/comment/add")
    public String addComment(Model model, RedirectAttributes attributes, @ModelAttribute @Validated CommentForm form,
            BindingResult bindingResult, @PathVariable String uid) {
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

            return "redirect:/" + uid + "/logs"; // ログイン後のホーム
        }

        attributes.addFlashAttribute("commentCreated", true); // 作業ログ作成完了フラグオン

        return "redirect:/" + uid + "/logs"; // ログイン後のホーム
    }

    /* -------------------- 成果追加 -------------------- */

    /**
     * 成果追加フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @GetMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/mode/add/achivement")
    public String addAchivementForm(Model model, RedirectAttributes attributes, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        attributes.addFlashAttribute("addAchivementMode", true); // 成果追加フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
    }

    /**
     * 成果を追加する
     *
     * @param model
     * @param attributes
     * @param AchivementForm
     * @param bindingResult
     * @param uid
     * @param since
     * @param until
     * @param wid
     * @return 作業ログ編集ページ
     */
    @PostMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/add/achivement")
    public String addAchivement(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated AchivementForm form, BindingResult bindingResult, @PathVariable String uid,
            @PathVariable String since, @PathVariable String until, @PathVariable String wid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("addAchivementMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isAchivementFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        try {
            cs.createAchivement(form.toEntity()); // コメント追加
        } catch (Exception e) {
            attributes.addFlashAttribute("addAchivementMode", true); // コメント入力フラグオン

            attributes.addFlashAttribute("isWorkDoseNotExist", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
        }

        attributes.addFlashAttribute("achivementCreated", true); // 作業ログ作成完了フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
    }

    /* -------------------- コメント削除 -------------------- */

    /**
     * コメントを削除
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
    @PostMapping("/{uid}/logs/lookback/{since}~{until}/{wid}/comment/delete")
    public String deleteComment(Model model, RedirectAttributes attributes, @ModelAttribute @Validated CidForm form,
            BindingResult bindingResult, @PathVariable String uid, @PathVariable String since,
            @PathVariable String until, @PathVariable String wid) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("isCidFormError", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        try {
            cs.deleteComment(uid, form.getCid()); // コメントを削除
        } catch (Exception e) {
            attributes.addFlashAttribute("isCommentDoseNotExist", true); // エラーフラグオン

            return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // viewを転換
        }

        attributes.addFlashAttribute("commentDeleted", true); // コメント削除完了フラグオン

        return "redirect:/" + uid + "/logs/lookback/" + since + "~" + until + "/" + wid; // 作業ログ編集ページ
    }

    // /**
    // * コメントを全ての作業ログ編集ページから削除
    // *
    // * @param model
    // * @param attributes
    // * @param CidForm
    // * @param bindingResult
    // * @param uid
    // * @return 全ての作業ログ編集ページ
    // */
    // @PostMapping("/{uid}/comment/lookbackall/delete")
    // public String deleteCommentFromAllComents(Model model, RedirectAttributes
    // attributes,
    // @ModelAttribute @Validated CidForm form, BindingResult bindingResult,
    // @PathVariable String uid) {
    // if (bindingResult.hasErrors()) { // formのバリゼーション違反
    // attributes.addFlashAttribute("isCidFormError", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // try {
    // cs.deleteComment(uid, form.getCid()); // コメントを削除
    // } catch (Exception e) {
    // attributes.addFlashAttribute("isCommentDoseNotExist", true); // エラーフラグオン

    // return "redirect:/" + uid + "/logs/lookbackall"; // viewを転換
    // }

    // return "redirect:/" + uid + "/logs/lookbackall"; // 全ての作業ログ編集ページ
    // }
}
