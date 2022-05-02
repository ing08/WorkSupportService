package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 所得する作業ログの期間指定フォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryForm {
    /** いつから(日付) */
    @NotNull
    private String since;

    /** いつまで(日付) */
    @NotNull
    private String until;

    // /** どの作業を */
    // @NotNull
    // private String wid;
}
