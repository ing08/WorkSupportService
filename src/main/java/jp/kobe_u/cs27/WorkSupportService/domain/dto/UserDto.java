package jp.kobe_u.cs27.WorkSupportService.domain.dto;

import java.util.List;

import lombok.Data;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.User;

/**
 * UserのDTO // 未使用
 *
 * @author ing
 */
@Data
public class UserDto {
    /** ユーザID */
    private String uid;

    /** 名前 */
    private String nickname;

    /** E-mail */
    private String email;

    /** 属しているコミュニティ一覧 */
    private List<String> comunities;

    /**
     * UserからDTOを生成
     *
     * @param user
     * @return UserDto
     */
    public static UserDto build(User user) {
        UserDto dto = new UserDto();

        dto.uid = user.getUid();
        dto.nickname = user.getNickname();
        dto.email = user.getEmail();

        return dto;
    }
}