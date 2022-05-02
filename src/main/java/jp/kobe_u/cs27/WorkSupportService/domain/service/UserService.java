package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import javax.transaction.Transactional;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;

import java.util.Date;

import jp.kobe_u.cs27.WorkSupportService.configration.exception.UserValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.WorkState;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.UserRepositry;

/**
 * ユーザを扱うサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class UserService {
    /** ユーザのリポジトリ */
    private final UserRepositry ur;

    /** 作業状況のサービス */
    private final WorkStateService wss;
    /** 作業イベントのサービス */
    private final WorkEventService wes;
    /** 作業ログのサービス */
    private final WorkLogService wls;
    /** コメントのサービス */
    private final CommentService cs;
    /** ログインログのサービス */
    private final LoginLogService lls;
    /** 目標時間設定のサービス */
    private final WorkTimeChallengeService wtcs;

    /* -------------------- Create -------------------- */

    /**
     * ユーザ新規登録
     *
     * @param user User
     * @return User
     */
    @Transactional
    public User createUser(User user) {
        String uid = user.getUid();

        if (ur.existsById(uid)) {// uidがかぶっているかどうかチェック
            throw new UserValidationException(USER_ALREADY_EXISTS, "create the user",
                    String.format("user %s already exists", user.getUid()));
        }

        // if (user.getNickname() == null) { // nicknameがnullの場合、例外を投げる
        // throw new UserValidationException(USER_NICKNAME_CAN_NOT_BE_SET_NULL, "create
        // the user",
        // String.format("user %s nickname can not be set null", user.getUid()));
        // }

        // if (user.getEmail() == null) { // emailがnullの場合、例外を投げる
        // throw new UserValidationException(USER_EMAIL_CAN_NOT_BE_SET_NULL, "create the
        // user",
        // String.format("user %s email can not be set null", user.getUid()));
        // }

        user.setCreatedAt(new Date()); // ユーザ作成日を記録

        User createduser = ur.save(user); // ユーザをDBに登録

        wss.createWorkState(uid, user.isDiscloseflag()); // ユーザの作業状況登録

        lls.createLoginLog(uid); // ユーザのログインログ登録

        return createduser; // 登録したユーザの情報を戻り値として返す
    }

    /* -------------------- Read -------------------- */

    /**
     * 指定したユーザが存在するか確認する
     *
     * @param uid
     * @return bool値
     */
    public boolean existsUser(String uid) {
        return ur.existsById(uid);
    }

    /**
     * ユーザを取得する
     *
     * @param uid
     * @return User
     */
    public User getUser(String uid) {
        User user = ur.findById(uid).orElseThrow(() -> new UserValidationException(USER_DOES_NOT_EXIST, "get the user",
                String.format("user %s does not exist", uid)));

        return user;
    }

    /* -------------------- Update -------------------- */

    /**
     * ユーザの情報を更新する
     *
     * @param user User
     * @return 更新したユーザの情報
     */
    @Transactional
    public User updateUser(String uid, User user) {
        // // ユーザIDを変数に格納する
        // final String uid = user.getUid();

        // ユーザIDを変数に格納する
        final String uidafter = user.getUid();

        // ユーザが存在しない場合、例外を投げる
        if (!ur.existsById(uid)) {
            throw new UserValidationException(USER_DOES_NOT_EXIST, "update the user",
                    String.format("user %s does not exist", uid));
        }

        // uidが変更されている場合、例外を投げる
        if (!uidafter.equals(uid)) {
            throw new UserValidationException(UID_CAN_NOT_BE_CHANGED, "update the user",
                    String.format("uid %s can not be changed", uid));
        }

        // if (user.getNickname() == null) { // nicknameがnullの場合、例外を投げる
        // throw new UserValidationException(USER_NICKNAME_CAN_NOT_BE_SET_NULL, "create
        // the user",
        // String.format("user %s nickname can not be set null", user.getUid()));
        // }

        // if (user.getEmail() == null) { // emailがnullの場合、例外を投げる
        // throw new UserValidationException(USER_EMAIL_CAN_NOT_BE_SET_NULL, "create the
        // user",
        // String.format("user %s email can not be set null", user.getUid()));
        // }

        user.setCreatedAt(ur.findById(uid).get().getCreatedAt()); // ユーザ作成日時を引き継ぎ

        wss.updateWorkState(uid, new WorkState(uid, wss.getWorkState(uid).getWid(), user.isDiscloseflag())); // DB上のユーザの作業状況を更新

        // DB上のユーザ情報を更新し、新しいユーザ情報を戻り値として返す
        return ur.save(user);
    }

    /* -------------------- Delete -------------------- */

    /**
     * 既存ユーザを退会させる
     *
     * @param uid
     */
    @Transactional
    public void deleteUser(String uid) {
        if (!ur.existsById(uid)) { // 退会させるUserが存在するかのチェック
            throw new UserValidationException(USER_DOES_NOT_EXIST, "delete the user",
                    String.format("user %s does not exist", uid));
        }

        // 各種ユーザ関連削除
        wss.deleteWorkState(uid);
        wes.deleteAllWorkEventByUid(uid);
        wls.deleteAllWorkLogByUid(uid);
        cs.deleteAllCommnetByUid(uid);
        lls.deleteLoginLog(uid);
        wtcs.deleteAllWorkTimeChallengeByUid(uid);

        ur.deleteById(uid); // ユーザを削除する
    }
}