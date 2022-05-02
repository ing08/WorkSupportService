package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 作業ログフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogForm {
    /** 作業ID */
    @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
    @NotNull
    private String wid;

    /** いつから(日付) */
    @NotNull
    private String datesince;

    /** いつから(時刻) */
    @NotNull
    private String timesince;

    /** いつまで(日付) */
    @NotNull
    private String dateuntil;

    /** いつまで(時刻) */
    @NotNull
    private String timeuntil;
}
