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

import jp.kobe_u.cs27.WorkSupportService.application.form.UidForm;
import jp.kobe_u.cs27.WorkSupportService.application.form.UserForm;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.UserValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import jp.kobe_u.cs27.WorkSupportService.domain.service.UserService;

/**
 * ユーザのコントローラー
 *
 * @author ing
 */
@Controller
@RequiredArgsConstructor
public class UserController {
    /** ユーザのサービス */
    private final UserService us;

    /**
     * ログインした後、WSServiceのページを返す
     *
     * @param model
     * @param attributes
     * @param UidForm
     * @param bindingResult
     * @return logs
     */
    @GetMapping("/user/enter")
    public String confirmUserExistence(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated UidForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // ユーザIDに使用できない文字が含まれていた場合
            attributes.addFlashAttribute("isUidValidationError", true); // エラーフラグをオンにする

            return "redirect:/"; // 初期画面に戻る
        }

        // ユーザIDを変数に格納する
        final String uid = form.getUid();

        // ユーザが登録済みかどうかの確認
        if (!us.existsUser(uid)) {
            attributes.addFlashAttribute("isUserDoesNotExistError", true); // エラーフラグオン

            return "redirect:/"; // viewを転換
        }

        // String nickname;

        // // ユーザIDから名前を取得する
        // // ユーザが登録済みかどうかの確認も兼ねている
        // try {
        // nickname = us.getUser(uid).getNickname();
        // } catch (UserValidationException e) { // ユーザIDが未登録であった場合
        // attributes.addFlashAttribute("isUserDoesNotExistError", true); //
        // エラーフラグをオンにする

        // // System.out.println(e.getMessage());

        // return "redirect:/"; // 初期ページに戻る
        // }

        // // ユーザIDとニックネームをModelに追加する
        // model.addAttribute("uid", uid);
        // model.addAttribute("nickname", nickname);

        return "redirect:/" + uid + "/logs"; // ホーム
    }

    /* -------------------- 新規登録 -------------------- */

    /**
     * 新規登録用ページを渡す
     *
     * @param model
     * @return userRegister
     */
    @GetMapping("/user/signup")
    public String showUserRegistrationPage(Model model) {
        // UserFormをModelに追加する(Thymeleaf上ではuserForm)
        model.addAttribute(new UserForm());

        return "userRegister";
    }

    /**
     * 新規登録を確認する
     *
     * @param model
     * @param attributes
     * @param UserForm
     * @param bindingResult
     * @return userConfirmRegistration
     */
    @GetMapping("/user/register/confirm")
    public String confirmUserRegistration(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated UserForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("isUserFormError", true); // エラーフラグオン

            return "redirect:/user/signup"; // viewを転換
        }

        final String uid = form.getUid(); // ユーザIDを変数に格納する

        if (us.existsUser(uid)) { // user登録済みの場合
            attributes.addFlashAttribute("isUserAlreadyExistsError", true); // エラーフラグオン

            return "redirect:/user/signup"; // viewを転換
        }

        // ユーザ情報をModelに追加する
        model.addAttribute("uid", uid);
        model.addAttribute("nickname", form.getNickname());
        model.addAttribute("email", form.getEmail());
        model.addAttribute("todoid", form.getTodoid());
        model.addAttribute("discloseflag", form.isDiscloseflag());

        return "userConfirmRegistration"; // ユーザ登録確認ページ
    }

    /**
     * 新規登録する
     *
     * @param model
     * @param attributes
     * @param UserForm
     * @param bindingResult
     * @return userRegistered
     */
    @PostMapping("/user/register")
    public String registerUser(Model model, RedirectAttributes attributes, @ModelAttribute @Validated UserForm form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // formのバリゼーション違反
            attributes.addFlashAttribute("isUserFormError", true); // エラーフラグオン

            return "redirect:/user/signup"; // viewを転換
        }

        try {
            us.createUser(form.toEntity()); // user登録
        } catch (UserValidationException e) { // user登録済みの場合
            attributes.addFlashAttribute("isUserAlreadyExistError", true); // エラーフラグオン

            System.out.println(e.getMessage());

            return "redirect:/user/signup"; // viewを転換
        }

        // ユーザ情報をModelに追加する
        // model.addAttribute(new UidForm());
        model.addAttribute("uid", form.getUid());
        model.addAttribute("nickname", form.getNickname());
        model.addAttribute("email", form.getEmail());
        model.addAttribute("todoid", form.getTodoid());
        model.addAttribute("discloseflag", form.isDiscloseflag());

        return "userRegistered"; // 新規登録完了ページを返す
    }

    /* -------------------- 情報更新 -------------------- */

    /**
     * ユーザの情報を取得し、確認画面を表示する
     *
     * @param model
     * @param attributes
     * @param uid
     * @return userInformation
     */
    @GetMapping("/{uid}/information")
    public String searchUserInformation(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        // // ユーザIDに使用できない文字が含まれていた場合
        // if (bindingResult.hasErrors()) {
        // // エラーフラグをオンにする
        // attributes.addFlashAttribute("isUidValidationError", true);

        // // 初期画面に戻る
        // return "redirect:/";
        // }

        // // ユーザIDを変数に格納する
        // final String uid = form.getUid();

        // ユーザ情報をDBから取得する
        // ユーザが登録済みかどうかの確認も兼ねている
        User user;
        try {
            user = us.getUser(uid);
        } catch (UserValidationException e) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserDoesNotExistError", true);
            // 初期ページに戻る
            return "redirect:/";
        }

        // ユーザ情報をModelに登録する
        model.addAttribute("uid", uid);
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("todoid", user.getTodoid());
        model.addAttribute("discloseflag", user.isDiscloseflag());

        // UserFormをModelに追加する(Thymeleaf上ではuserForm)
        model.addAttribute(new UserForm());

        // ユーザ情報確認ページ
        return "userInformation";
    }

    /**
     * ユーザ情報更新が可能か確認する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return userConfirmUpdate
     */
    @GetMapping("/{uid}/update/confirm")
    public String confirmUserUpdate(Model model, RedirectAttributes attributes,
            @ModelAttribute @Validated UserForm form, BindingResult bindingResult, @PathVariable String uid) {
        // フォームにバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserFormError", true);

            // // リダイレクト先の引数としてユーザIDを渡す
            // attributes.addAttribute("uid", form.getUid());

            // ユーザ情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/information";
        }

        // // ユーザIDを変数に格納する
        // final String uid = form.getUid();

        // ユーザが登録済みか確認する
        if (!us.existsUser(uid)) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserAlreadyExistsError", true);

            // // リダイレクト先の引数としてユーザIDを渡す
            // attributes.addAttribute("uid", form.getUid());

            // ユーザ情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/information";
        }

        // ユーザ情報をModelに追加する
        model.addAttribute("uid", uid);
        model.addAttribute("nickname", form.getNickname());
        model.addAttribute("email", form.getEmail());
        model.addAttribute("todoid", form.getTodoid());
        model.addAttribute("discloseflag", form.isDiscloseflag());

        // ユーザ情報更新確認ページ
        return "userConfirmUpdate";
    }

    /**
     * ユーザ情報を更新する
     *
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @param uid
     * @return userUpdated
     */
    @PostMapping("/{uid}/update")
    public String updateUser(Model model, RedirectAttributes attributes, @ModelAttribute @Validated UserForm form,
            BindingResult bindingResult, @PathVariable String uid) {
        // フォームにバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserFormError", true);

            // // リダイレクト先の引数としてユーザIDを渡す
            // attributes.addAttribute("uid", form.getUid());

            // ユーザ情報取得メソッドにリダイレクトする
            return "redirect:/" + uid + "/information";
        }

        // ユーザ情報を更新する
        try {
            us.updateUser(uid, form.toEntity());
        } catch (UserValidationException e) {
            // ユーザが存在しない場合
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserDoesNotExistError", true);
            // 初期ページに戻る
            return "redirect:/";
        }

        // ユーザIDとニックネームをModelに追加する
        model.addAttribute("uid", form.getUid());
        model.addAttribute("nickname", form.getNickname());
        model.addAttribute("email", form.getEmail());
        model.addAttribute("todoid", form.getTodoid());
        model.addAttribute("discloseflag", form.isDiscloseflag());

        // ユーザ情報更新完了ページ
        return "userUpdated";
    }

    /* -------------------- 退会 -------------------- */

    /**
     * ユーザ削除が可能か確認する
     *
     * @param model
     * @param attributes
     * @param uid
     * @return userConfirmDelete
     */
    @GetMapping("/{uid}/delete/confirm")
    public String confirmUserDelete(Model model, RedirectAttributes attributes, @PathVariable String uid) {
        // // ユーザIDに使用できない文字が含まれていた場合
        // if (bindingResult.hasErrors()) {
        // // エラーフラグをオンにする
        // attributes.addFlashAttribute("isUidValidationError", true);

        // // 初期画面に戻る
        // return "redirect:/";
        // }

        // // ユーザIDを変数に格納する
        // final String uid = form.getUid();

        // ユーザ情報をDBから取得する
        // ユーザが登録済みかどうかの確認も兼ねている
        User user;
        try {
            user = us.getUser(uid);
        } catch (UserValidationException e) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute("isUserDoesNotExistError", true);
            // 初期ページに戻る
            return "redirect:/";
        }

        // ユーザ情報をModelに追加する
        model.addAttribute("uid", uid);
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("todoid", user.getTodoid());

        // ユーザ削除確認ページ
        return "userConfirmDelete";
    }

    /**
     * ユーザを削除する
     *
     * @param model
     * @param uid
     * @return userDeleted
     */
    @PostMapping("/{uid}/delete")
    public String deleteUser(Model model, @PathVariable String uid) {
        // // ユーザIDに使用できない文字が含まれていた場合
        // if (bindingResult.hasErrors()) {
        // // エラーフラグをオンにする
        // attributes.addFlashAttribute("isUidValidationError", true);

        // // 初期画面に戻る
        // return "redirect:/";
        // }

        // // ユーザIDを変数に格納する
        // final String uid = form.getUid();

        // ユーザを削除する
        us.deleteUser(uid);

        // uidのみモデルに登録
        model.addAttribute("uid", uid);

        // 退会完了ページ
        return "userDeleted";
    }
}
