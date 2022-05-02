package jp.kobe_u.cs27.WorkSupportService.application.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 作業IDフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WidForm {
    /** 作業ID */
    @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
    @NotNull
    private String wid;

    // /** ユーザID */
    // @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
    // @NotNull
    // private String uid;
}
