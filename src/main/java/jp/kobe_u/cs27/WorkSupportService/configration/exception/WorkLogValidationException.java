package jp.kobe_u.cs27.WorkSupportService.configration.exception;

import lombok.Getter;

/**
 * 作業ログに関するバリデーション違反が発生した場合に発生する例外
 *
 * @author ing
 */
@Getter
public class WorkLogValidationException extends RuntimeException {
    /** シリアライズであることを保証？ */
    private final static long serialVersionUID = 1L;
    /** エラーの種類識別子 */
    private final ErrorCode code;

    /**
     * 例外を生成するコンストラクタ
     *
     * @param code  エラーコード
     * @param error 発生したエラーの内容を説明する文字列
     * @param cause 発生したエラーの原因を説明する文字列
     */
    public WorkLogValidationException(ErrorCode code, String error, String cause) {
        super(String.format("fail to %s, because %s.", error, cause));

        this.code = code;
    }
}
