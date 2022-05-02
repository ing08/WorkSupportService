package jp.kobe_u.cs27.WorkSupportService.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static jp.kobe_u.cs27.WorkSupportService.configration.exception.ErrorCode.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import jp.kobe_u.cs27.WorkSupportService.configration.exception.LoginLogValidationException;
import jp.kobe_u.cs27.WorkSupportService.configration.exception.UserValidationException;
import jp.kobe_u.cs27.WorkSupportService.domain.entity.LoginLog;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.LoginLogRepositry;
import jp.kobe_u.cs27.WorkSupportService.domain.repositry.UserRepositry;

/**
 * ログインログのサービス
 *
 * @author ing
 */
@Service
@RequiredArgsConstructor
public class LoginLogService {
    /** ログインログのリポジトリ */
    private final LoginLogRepositry llr;
    /** ユーザのリポジトリ */
    private final UserRepositry ur;

    /* -------------------- Create -------------------- */

    /**
     * ログインログを作成
     *
     * @param uid
     * @return LoginLog
     */
    public LoginLog createLoginLog(String uid) {
        if (!ur.existsById(uid)) {// ユーザが存在するかどうかチェック
            throw new UserValidationException(USER_DOES_NOT_EXIST, "create the loginLog",
                    String.format("user %s does not exist", uid));
        }

        if (llr.existsById(uid)) {// ログインログにおいてuidがかぶっているかどうかチェック
            throw new LoginLogValidationException(LOGINLOG_ALREADY_EXISTS, "create the loginLog",
                    String.format("user %s loginLog already exists", uid));
        }

        return llr.save(new LoginLog(uid, null, 0, 0)); // ログインログをDBに登録し、登録したログインログの情報を戻り値として返す
    }

    /* -------------------- Read -------------------- */

    /**
     * ログインログを所得
     *
     * @param uid
     * @return
     */
    public LoginLog getLoginLog(String uid) {
        LoginLog log = llr.findById(uid).orElseThrow(() -> new LoginLogValidationException(LOGINLOG_DOES_NOT_EXIST,
                "get the loginLog", String.format("user %s loginLog does not exist", uid)));

        return log;
    }

    /* -------------------- Update -------------------- */

    /**
     * ログインログを更新
     *
     * @param uid
     * @param lastlogin
     * @return LoginLog
     */
    public LoginLog updateLoginLog(String uid, Date lastlogin) {
        LoginLog log = this.getLoginLog(uid); // ログインログを所得

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 前日の日付を所得
        cal.setTime(lastlogin);
        cal.add(Calendar.DATE, -1);
        String lastlogindaybefore = sdFormat.format(cal.getTime());

        String newlastloginst = sdFormat.format(lastlogin); // Date型をString型に変換する

        Date oldlastlogin = log.getLastlogin();

        String oldlastloginst = new String();

        if (oldlastlogin != null) {
            oldlastloginst = sdFormat.format(oldlastlogin); // Date型をString型に変換する
        }

        int loginstreak = log.getLoginstreak(); // 現在の連続ログインに数を所得
        int maximumloginstreak = log.getMaximumloginstreak();

        if (!newlastloginst.equals(oldlastloginst)) { // その日初めてのログ記録ならば
            if (loginstreak == 0) {
                loginstreak++;
            } else {
                if (oldlastloginst.equals(lastlogindaybefore)) { // 連続ログインならば
                    loginstreak++;
                } else {
                    loginstreak = 1;
                }
            }

            if (loginstreak > maximumloginstreak) { // 最大連続ログイン記録を更新していれば
                maximumloginstreak = loginstreak; // 記録を更新

                // 最大連続ログイン記録をセット
                log.setMaximumloginstreak(maximumloginstreak);
            }

            // 現在の連続ログイン記録をセット
            log.setLoginstreak(loginstreak);
        }

        // 最新ログイン記録をセット
        log.setLastlogin(lastlogin);

        return llr.save(log); // DB上のログインログ情報を更新
    }

    /* -------------------- Delete -------------------- */

    /**
     * ログインログを削除
     *
     * @param uid
     */
    @Transactional
    public void deleteLoginLog(String uid) {
        // ログインログが存在しない場合、例外を投げる
        if (!llr.existsById(uid)) {
            throw new LoginLogValidationException(LOGINLOG_DOES_NOT_EXIST, "delete the loginLog",
                    String.format("user %s loginLog does not exist", uid));
        }

        llr.deleteById(uid);
    }
}