package jp.kobe_u.cs27.WorkSupportService.application.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.LoginLog;
import jp.kobe_u.cs27.WorkSupportService.domain.service.LoginLogService;

/**
 * ログインログを操作するAPIを定義するRESTコントローラ
 *
 * @author ing
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginLogRestController {
    /** ログインログのサービス */
    private final LoginLogService lls;

    /* -------------------- Read -------------------- */

    /**
     * あるユーザのログインログを所得
     *
     * @param uid
     * @return
     */
    @GetMapping("/loginlog/{uid}")
    public LoginLog getLoginLog(@PathVariable String uid) {
        return lls.getLoginLog(uid);
    }
}
