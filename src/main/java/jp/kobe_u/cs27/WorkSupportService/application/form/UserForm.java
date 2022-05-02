package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
// import lombok.NonNull;
import lombok.Setter;

/**
 * ユーザーフォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
    /** ユーザID */
    @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
    @NotNull
    private String uid;

    /** 名前 */
    // @NonNull
    @NotBlank
    private String nickname;

    /** メールアドレス */
    @NotBlank
    @Email
    private String email;

    /** 情報を公開するか */
    @NotNull
    private boolean discloseflag;

    /** ToDoリストのユーザID */
    private String todoid;

    /**
     * フォームをエンティティに変換
     *
     * @return User
     */
    public User toEntity() {
        return new User(this.getUid(), this.getNickname(), this.getEmail(), this.isDiscloseflag(), null,
                this.getTodoid());
    }
}
