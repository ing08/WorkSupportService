package jp.kobe_u.cs27.WorkSupportService.application.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 作業ログIDフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LidForm {
    /** 作業ログID */
    @NotNull
    private Long lid;
}
