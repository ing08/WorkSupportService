package jp.kobe_u.cs27.WorkSupportService.application.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * コメントIDフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CidForm {
    /** コメントID */
    @NotNull
    private Long cid;
}
