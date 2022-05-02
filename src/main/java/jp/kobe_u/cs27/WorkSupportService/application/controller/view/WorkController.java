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

import jp.kobe_u.cs27.WorkSupportService.application.form.WidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.WorkForm;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.WorkValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.service.WorkService;

/**
 * 作業のコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class WorkController {
    /** 作業のサービス */
    private final WorkService ws;

    /**
     * 作業の編集ページを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @return workInformation
     */
    @GetMapping("/{uid}/work/information")
    public String showWorkControllPage(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        // if (bindingResult.hasErrors()) { // ユーザIDに使用できない文字が含まれていた場合
        // attributes.addFlashAttribute("isUidValidationError", true); // エラーフラグをオンにする

        // return "redirect:/"; // 初期画面に戻る
        // }

        // model.addAttribute("uid", form.getUid()); // ユーザIDをModelに追加する

        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        model.addAttribute("works", ws.getAllWork()); // 作業リストをModelに追加

        model.addAttribute(new WidForm()); // WidFormをModelに追加

        model.addAttribute(new WorkForm()); // WorkFormをModelに追加

        return "workInformation"; // 作業の編集ページ
    }

    /* -------------------- 新規登録 -------------------- */

    /**
     * 新規登録用フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @return 作業の編集ページ
     */
    @GetMapping("/{uid}/work/mode/register")
    private String addWorkRegistrationForm(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        // attributes.addAttribute("uid", form.getUid()); // ユーザIDをModelに追加する

        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        attributes.addFlashAttribute("WorkRegistrationMode", true); // 作業登録フラグオン

        // attributes.addAttribute(new WorkForm()); // WorkFormをModelに追加

        return "redirect:/" + uid + "/work/information"; // viewを転換
    }

    /**
     * 新規登録する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return 作業の編集ページ
     */
    @PostMapping("/{uid}/work/register")
    public String registerWork(Model model, RedirectAttributes attributes, @ModelAttribute @Validated WorkForm form,
            BindingResult bindingResult, @PathVariable String uid) {
        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("WorkRegistrationMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isWorkFormError", true); // エラーフラグオン

            // attributes.addAttribute(new WorkForm()); // WorkFormをModelに追加

            return "redirect:/" + uid + "/work/information"; // viewを転換
        }

        try {
            ws.createWork(form.toEntity()); // 作業登録
        } catch (WorkValidationException e) { // 作業登録済みの場合
            attributes.addFlashAttribute("WorkRegistrationMode", true); // 作業登録フラグオン

            attributes.addFlashAttribute("isWorkAlreadyExistError", true); // エラーフラグオン

            // attributes.addAttribute(new WorkForm()); // WorkFormをModelに追加

            return "redirect:/" + uid + "/work/information"; // viewを転換
        }

        return "redirect:/" + uid + "/work/information"; // 作業の編集ページ
    }

    /* -------------------- 情報更新 -------------------- */

    /**
     * 情報更新用フォームを渡す
     *
     * @param model
     * @param attributes
     * @param uid
     * @return 作業の編集ページ
     */
    @GetMapping("/{uid}/work/mode/update")
    private String addWorkUpdateForm(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        attributes.addFlashAttribute("WorkUpdateMode", true); // 作業情報更新フラグオン

        return "redirect:/" + uid + "/work/information"; // viewを転換
    }

    /**
     * 作業情報を更新する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return 作業の編集ページ
     */
    @PostMapping("/{uid}/work/update")
    public String updateWork(Model model, RedirectAttributes attributes, @ModelAttribute @Validated WorkForm form,
            BindingResult bindingResult, @PathVariable String uid) {
        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        System.out.println(form.getWid() + " : " + form.getWname());

        // フォームにバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("WorkUpdateMode", true); // 作業情報更新フラグオン

            // エラーフラグをオンにする
            attributes.addFlashAttribute("isWorkFormError", true);

            // // リダイレクト先の引数としてユーザIDを渡す
            // attributes.addAttribute("uid", form.getUid());

            // 作業情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/work/information";
        }

        // 作業情報を更新する
        try {
            ws.updateWork(form.getWid(), form.toEntity());
        } catch (WorkValidationException e) {// 作業が存在しない場合 or 作業中のユーザがいる場合
            attributes.addFlashAttribute("WorkUpdateMode", true); // 作業情報更新フラグオン

            // エラーフラグをオンにする
            attributes.addFlashAttribute("wasWorkNotBeUpdatedError", true);

            // 作業情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/work/information";
        }

        return "redirect:/" + uid + "/work/information"; // 作業の編集ページ
    }

    /* -------------------- 削除 -------------------- */

    /**
     * 作業を削除する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return 作業の編集ページ
     */
    @PostMapping("/{uid}/work/delete")
    public String deleteUser(Model model, RedirectAttributes attributes, @ModelAttribute @Validated WidForm form,
            BindingResult bindingResult, @PathVariable String uid) {
        model.addAttribute("uid", uid); // ユーザIDをModelに追加する

        // フォームにバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isWidFormError", true);

            // 作業情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/work/information";
        }

        // ユーザを削除する
        try {
            ws.deleteWork(form.getWid());
        } catch (WorkValidationException e) {// 作業が存在しない場合 or 作業中のユーザがいる場合
            // エラーフラグをオンにする
            attributes.addFlashAttribute("wasWorkNotBeDeletedError", true);

            // 作業情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/work/information";
        }

        return "redirect:/" + uid + "/work/information"; // 作業の編集ページ
    }
}
