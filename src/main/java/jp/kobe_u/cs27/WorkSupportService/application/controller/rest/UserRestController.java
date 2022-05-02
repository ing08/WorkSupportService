package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.application.form.UserForm;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import jp.kobe_u.cs27.WorkSupportService.domain.service.UserService;

/**
 * ユーザを操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    /** ユーザのサービス */
    private final UserService us;

    /* -------------------- Create -------------------- */

    /**
     * ユーザ登録
     *
     * @param UserForm
     * @return User
     */
    @PostMapping("/users")
    public User addUser(@RequestBody @Validated UserForm form) {
        User user = us.createUser(form.toEntity());
        return user;
    }

    /* -------------------- Read -------------------- */

    /**
     * ユーザの所得
     *
     * @param uid
     * @return User
     */
    @GetMapping("/users/{uid}")
    public User getUser(@PathVariable String uid) {
        return us.getUser(uid);
    }

    /* -------------------- Update -------------------- */

    /**
     * ユーザの情報更新
     *
     * @param uid, Userform
     * @return User
     */
    @PutMapping("/users/{uid}")
    public User updateUser(@PathVariable String uid, @RequestBody @Validated UserForm form) {
        User user = us.updateUser(uid, form.toEntity());
        return user;
    }

    /* -------------------- Delete -------------------- */

    /**
     * ユーザ退会
     *
     * @param uid
     * @return boolean
     */
    @DeleteMapping("/users/{uid}")
    public Boolean deleteUser(@PathVariable String uid) {
        us.deleteUser(uid);

        return true;
    }
}
